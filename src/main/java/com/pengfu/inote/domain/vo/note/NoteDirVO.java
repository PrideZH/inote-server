package com.pengfu.inote.domain.vo.note;

import com.pengfu.inote.domain.entity.NoteFolder;
import com.pengfu.inote.domain.enums.StatusEnum;
import com.pengfu.inote.domain.vo.common.DirectoryVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NoteDirVO extends DirectoryVO {

    private Long noteId;

    /** 文章状态 若未发布文章则为null */
    private StatusEnum status;

    public static NoteDirVO build(NoteFolder noteFolder, String noteName, StatusEnum status) {
        NoteDirVO instance = new NoteDirVO();

        instance.setId(noteFolder.getId());
        instance.setDirId("note_" + instance.getId());
        instance.setNote(true);
        instance.setParentId(noteFolder.getFolderId());
        instance.setNoteId(noteFolder.getNoteId());
        instance.setCreateTime(noteFolder.getCreateTime());
        instance.setUpdateTime(noteFolder.getUpdateTime());

        instance.setName(noteName);

        instance.setStatus(status);

        return instance;
    }

}