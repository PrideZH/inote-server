package com.pengfu.inote.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.dto.note.NotePatchDTO;
import com.pengfu.inote.domain.dto.note.NotePostDTO;
import com.pengfu.inote.domain.entity.*;
import com.pengfu.inote.domain.vo.article.ArticleInfoVO;
import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.domain.vo.note.NoteDirVO;
import com.pengfu.inote.domain.vo.note.NoteInfoVO;
import com.pengfu.inote.manager.FileManager;
import com.pengfu.inote.manager.FolderManager;
import com.pengfu.inote.manager.NoteManager;
import com.pengfu.inote.manager.TagManager;
import com.pengfu.inote.mapper.*;
import com.pengfu.inote.service.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@AllArgsConstructor
@Service
public class NoteService {

    private NoteMapper noteMapper;
    private NoteFolderMapper noteFolderMapper;
    private ArticleMapper articleMapper;
    private TagManager tagManager;

    private NoteManager noteManager;
    private FileManager fileManager;
    private FolderManager folderManager;

    private static final String NOTE_URL = "/note/";

    /**
     * 当前用户添加笔记
     */
    @Transactional(rollbackFor = Exception.class)
    public NoteDirVO add(NotePostDTO notePostDTO) throws Exception {
        if (notePostDTO.getFolderId() != null && notePostDTO.getFolderId() != 0) {
            folderManager.checkExist(notePostDTO.getFolderId());
        }

        Note note = new Note();
        BeanUtils.copyProperties(notePostDTO, note);
        noteMapper.insert(note);

        // 创建文件并更新文件路径
        String name = "note_" + note.getId();
        String fileUrl = NOTE_URL + name + ".md";
        if (!fileManager.createFile(fileUrl)) {
            throw new ServiceException(1001, "文件创建失败");
        }

        // 更新数据
        note.setFileUrl(fileUrl);
        note.setName(name);
        noteMapper.updateById(note);

        // 添加与文件夹的关联
        NoteFolder noteFolder = null;
        if(notePostDTO.getFolderId() != null) {
            noteFolder = new NoteFolder();
            BeanUtils.copyProperties(notePostDTO, noteFolder);
            noteFolder.setNoteId(note.getId());
            noteFolderMapper.insert(noteFolder);
        }

        NoteDirVO noteDirVO = new NoteDirVO();
        noteDirVO.setNote(true);
        // 无关联文件夹则返回未关联属性
        if(noteFolder != null) {
            BeanUtils.copyProperties(noteFolder, noteDirVO);
            noteDirVO.setParentId(noteFolder.getFolderId());
            noteDirVO.setDirId("note_" + noteDirVO.getId());
        } else {
            BeanUtils.copyProperties(note, noteDirVO);
            noteDirVO.setNoteId(note.getId());
            noteDirVO.setParentId(0L);
            noteDirVO.setDirId(String.valueOf(note.getId()));
        }
        return noteDirVO;
    }

    public List<NoteDirVO> getMeDiscrete() {
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString());

        List<Note> notes = noteMapper.selectList(new QueryWrapper<Note>().lambda()
                .eq(Note::getUserId, userId)
                .notExists("SELECT * FROM note__folder WHERE note__folder.note_id = note.id"));

        List<NoteDirVO> noteDirVOList = new ArrayList<>();
        for (Note note : notes) {
            NoteDirVO noteDirVO = new NoteDirVO();
            BeanUtils.copyProperties(note, noteDirVO);
            noteDirVO.setNote(true);
            noteDirVO.setNoteId(note.getId());
            noteDirVO.setDirId(String.valueOf(note.getId()));
            noteDirVO.setParentId(0L);
            noteDirVOList.add(noteDirVO);
        }
        return noteDirVOList;
    }

    public List<NoteDirVO> getMeRecentness(Integer days) {
        Long userId = Long.valueOf(StpUtil.getLoginIdAsString());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);

        List<Note> notes = noteMapper.selectList(new QueryWrapper<Note>()
                .eq("user_id", userId)
                .ge("update_time", cal.getTime())
                .orderByDesc("update_time"));

        ArrayList<NoteDirVO> noteDirVOList = new ArrayList<>();
        for (Note note : notes) {
            NoteDirVO noteDirVO = new NoteDirVO();
            BeanUtils.copyProperties(note, noteDirVO);
            noteDirVO.setNote(true);
            noteDirVO.setNoteId(note.getId());
            noteDirVO.setDirId(String.valueOf(note.getId()));
            noteDirVO.setParentId(0L);
            noteDirVOList.add(noteDirVO);
        }
        return noteDirVOList;
    }

    public NoteInfoVO get(Long id) throws Exception {
        Note note = noteManager.getById(id);

        Long loginId = Long.valueOf(StpUtil.getLoginIdAsString());
        if (!note.getUserId().equals(loginId)) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        NoteInfoVO noteInfoVO = new NoteInfoVO();
        BeanUtils.copyProperties(note, noteInfoVO);

        // 获取文件内容
        byte[] bytes = fileManager.read(note.getFileUrl());
        noteInfoVO.setContent(new String(bytes));

        // 若发布了文章则获取文章信息
        if (note.getArticleId() != null) {
            Article article = articleMapper.selectById(note.getArticleId());

            ArticleInfoVO articleInfoVO = new ArticleInfoVO();
            BeanUtils.copyProperties(article, articleInfoVO);

            // 获取文章标签
            articleInfoVO.setTagNames(tagManager.getTagNameListByArticle(article.getId()));

            noteInfoVO.setArticle(articleInfoVO);
        }

        return noteInfoVO;
    }

    public NoteInfoVO update(NotePatchDTO notePatchDTO) throws Exception {
        Note note = noteManager.getById(notePatchDTO.getId());

        Long userId = StpUtil.getLoginIdAsLong();
        if (!note.getUserId().equals(userId)) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        Note noteValue = new Note();
        BeanUtils.copyProperties(notePatchDTO, noteValue);
        noteMapper.updateById(noteValue);

        note = noteManager.getById(notePatchDTO.getId());
        if (notePatchDTO.getContent() != null) {
            fileManager.write(note.getFileUrl(), notePatchDTO.getContent());
        }

        NoteInfoVO noteInfoVO = new NoteInfoVO();
        BeanUtils.copyProperties(note, noteInfoVO);
        return noteInfoVO;
    }

    @Transactional(rollbackFor = Exception.class)
    public void del(Long id) {
        Note note = noteManager.getById(id);

        Long userId = StpUtil.getLoginIdAsLong();
        if (!note.getUserId().equals(userId)) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        // 删除文章
        articleMapper.deleteById(note.getArticleId());

        // 删除关联
        noteFolderMapper.delete(new QueryWrapper<NoteFolder>().lambda().eq(NoteFolder::getNoteId, id));

        // 删除文件
        if(!fileManager.delete(note.getFileUrl())) {
            throw new ServiceException(1001, "文件删除失败");
        }

        // 删除笔记
        noteMapper.deleteById(id);
    }

}
