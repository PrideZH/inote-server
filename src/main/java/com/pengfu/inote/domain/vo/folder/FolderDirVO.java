package com.pengfu.inote.domain.vo.folder;

import com.pengfu.inote.domain.vo.common.DirectoryVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FolderDirVO extends DirectoryVO {

    private List<FolderDirVO> children;

}