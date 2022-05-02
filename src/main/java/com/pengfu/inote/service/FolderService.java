package com.pengfu.inote.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pengfu.inote.domain.dto.folder.FolderPatchDTO;
import com.pengfu.inote.domain.dto.folder.FolderPostDTO;
import com.pengfu.inote.domain.dto.noteFolder.NoteFolderPatchDTO;
import com.pengfu.inote.domain.dto.noteFolder.NoteFolderPostDTO;
import com.pengfu.inote.domain.entity.Article;
import com.pengfu.inote.domain.entity.Folder;
import com.pengfu.inote.domain.entity.Note;
import com.pengfu.inote.domain.entity.NoteFolder;
import com.pengfu.inote.domain.enums.StatusEnum;
import com.pengfu.inote.domain.vo.common.DirectoryVO;
import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.domain.vo.folder.FolderDirVO;
import com.pengfu.inote.domain.vo.note.NoteDirVO;
import com.pengfu.inote.manager.FolderManager;
import com.pengfu.inote.manager.NoteManager;
import com.pengfu.inote.mapper.ArticleMapper;
import com.pengfu.inote.mapper.FolderMapper;
import com.pengfu.inote.mapper.NoteFolderMapper;
import com.pengfu.inote.service.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class FolderService {

    private FolderMapper folderMapper;
    private NoteFolderMapper noteFolderMapper;
    private ArticleMapper articleMapper;

    private FolderManager folderManager;
    private NoteManager noteManager;

    public FolderDirVO addMe(FolderPostDTO folderPostDTO) {
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString());

        if (folderPostDTO.getParentId() != 0) {
            Folder folder = folderManager.getById(folderPostDTO.getParentId());
            if (!folder.getUserId().equals(userId)) {
                throw new ServiceException(ResultCode.FORBIDDEN);
            }
        }

        Folder folder = folderMapper.selectOne(new QueryWrapper<Folder>().lambda()
                .eq(Folder::getUserId, userId)
                .eq(Folder::getParentId, folderPostDTO.getParentId())
                .eq(Folder::getName, folderPostDTO.getName()));
        if (folder != null) {
            throw new ServiceException(1001, "文件夹名重复");
        }

        folder = new Folder();
        BeanUtils.copyProperties(folderPostDTO, folder);
        folder.setUserId(userId);
        folderMapper.insert(folder);

        FolderDirVO folderDirVO = new FolderDirVO();
        BeanUtils.copyProperties(folder, folderDirVO);
        folderDirVO.setNote(false);
        folderDirVO.setChildren(new ArrayList<>());
        folderDirVO.setDirId("folder_" + folder.getId());
        return folderDirVO;
    }

    /**
     * 添加文件关联
     */
    public NoteDirVO addRelevance(NoteFolderPostDTO noteFolderPostDTO) {
        Note note = noteManager.getById(noteFolderPostDTO.getNoteId());
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString());

        // 权限判断
        if (!note.getUserId().equals(userId)) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        if (noteFolderPostDTO.getFolderId() != 0) {
            Folder folder = folderManager.getById(noteFolderPostDTO.getFolderId());
            if (!folder.getUserId().equals(userId)) {
                throw new ServiceException(ResultCode.FORBIDDEN);
            }
        }

        // 插入关联文件
        NoteFolder noteFolder = new NoteFolder();
        BeanUtils.copyProperties(noteFolderPostDTO, noteFolder);
        noteFolderMapper.insert(noteFolder);

        NoteDirVO noteDirVO = new NoteDirVO();
        BeanUtils.copyProperties(noteFolder, noteDirVO);
        noteDirVO.setDirId("note_" + noteFolder.getId());
        noteDirVO.setParentId(noteFolder.getFolderId());
        noteDirVO.setNote(true);
        return noteDirVO;
    }


    public DirectoryVO get(Long id) {
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString());

        Folder folder = folderManager.getById(id);
        if (!folder.getUserId().equals(userId)) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        FolderDirVO folderDirVO = new FolderDirVO();
        BeanUtils.copyProperties(folder, folderDirVO);
        folderDirVO.setDirId("folder_" + folderDirVO.getId());
        folderDirVO.setChildren(new ArrayList<>());
        folderDirVO.setNote(false);
        return folderDirVO;
    }

    /**
     * 获取当前用户文件目录信息
     */
    public List<DirectoryVO> getMeDir(Long id) {
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString());

        if (id != 0) {
            Folder folderCheck = folderManager.getById(id);
            if (!folderCheck.getUserId().equals(userId)) {
                throw new ServiceException(ResultCode.FORBIDDEN);
            }
        }

        // 获取指定文件夹下的文件夹和笔记
        List<Folder> folders = folderMapper.selectList(new QueryWrapper<Folder>().lambda()
                .eq(Folder::getUserId, userId)
                .eq(Folder::getParentId, id));
        List<NoteFolder> noteFolders = noteFolderMapper.selectList(new QueryWrapper<NoteFolder>().lambda()
            .eq(NoteFolder::getFolderId, id));

        // 添加文件夹列表
        List<DirectoryVO> directoryVOList = new ArrayList<>(folders.stream().map(folder -> {
            FolderDirVO folderDirVO = new FolderDirVO();
            BeanUtils.copyProperties(folder, folderDirVO);
            folderDirVO.setDirId("folder_" + folderDirVO.getId());
            folderDirVO.setChildren(new ArrayList<>());
            folderDirVO.setNote(false);
            return folderDirVO;
        }).toList());

        // 添加笔记列表
        if (noteFolders != null) {
            directoryVOList.addAll(noteFolders.stream().map(noteFolder -> {
                Note note = noteManager.getById(noteFolder.getNoteId());
                StatusEnum status = null;
                if (note.getArticleId() != null) {
                    Article article = articleMapper.selectById(note.getArticleId());
                    status = article.getStatus();
                }
                return NoteDirVO.build(noteFolder, note.getName(), status);
            }).toList());
        }

        return directoryVOList;
    }

    public FolderDirVO update(FolderPatchDTO folderPatchDTO) {
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString());
        folderManager.checkExist(folderPatchDTO.getId());

        if (folderMapper.selectOne(new QueryWrapper<Folder>().lambda()
                .eq(Folder::getUserId, userId)
                .eq(Folder::getParentId, folderPatchDTO.getParentId())
                .eq(Folder::getName, folderPatchDTO.getName())) != null) {
            throw new ServiceException(1001, "文件夹名重复");
        }

        Folder folder = new Folder();
        BeanUtils.copyProperties(folderPatchDTO, folder);
        folderMapper.updateById(folder);

        FolderDirVO folderDirVO = new FolderDirVO();
        BeanUtils.copyProperties(folder, folderDirVO);
        folderDirVO.setNote(false);
        folderDirVO.setChildren(new ArrayList<>());
        folderDirVO.setDirId("folder_" + folder.getId());
        return folderDirVO;
    }

    public NoteDirVO updateRelevance(NoteFolderPatchDTO noteFolderPatchDTO) {
        NoteFolder noteFolder = new NoteFolder();
        BeanUtils.copyProperties(noteFolderPatchDTO, noteFolder);
        noteFolderMapper.updateById(noteFolder);

        NoteDirVO noteDirVO = new NoteDirVO();
        NoteFolder res = noteFolderMapper.selectOne(new QueryWrapper<NoteFolder>().lambda()
                .eq(NoteFolder::getId, noteFolderPatchDTO.getId()));
        BeanUtils.copyProperties(res, noteDirVO);
        noteDirVO.setNote(true);
        noteDirVO.setDirId("note_" + res.getId());
        return noteDirVO;
    }

    /**
     * 获取文件夹的所有子文件ID
     */
    private ArrayList<Long> getChildrenId(Long id) {
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(id);
        List<Folder> folders = folderMapper.selectList(new QueryWrapper<Folder>().lambda().eq(Folder::getParentId, id));
        for (Folder item : folders) {
            ids.addAll(getChildrenId(item.getId()));
        }
        return ids;
    }

    /**
     * 删除文件夹及其关联文件
     */
    public void del(Long id) {
        Folder folder = folderManager.getById(id);

        // 删除所有子文件夹
        ArrayList<Long> childrenIds = getChildrenId(id);
        folderMapper.deleteBatchIds(childrenIds);

        // 删除所有关联笔记
        for (Long childrenId : childrenIds) {
            noteFolderMapper.delete(new QueryWrapper<NoteFolder>().lambda().eq(NoteFolder::getFolderId, childrenId));
        }

        folderMapper.deleteById(folder);
    }

    /**
     * 删除关联文件
     */
    public void delRelevance(Long relevanceId) {
        NoteFolder noteFolder = noteFolderMapper.selectById(relevanceId);
        if (noteFolder == null) {
            throw new ServiceException(ResultCode.NOT_FOUND);
        }

        noteFolderMapper.deleteById(relevanceId);
    }

}
