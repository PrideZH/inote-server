package com.pengfu.inote.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pengfu.inote.domain.dto.common.PageDTO;
import com.pengfu.inote.domain.dto.reply.ReplyPostDTO;
import com.pengfu.inote.domain.entity.Like;
import com.pengfu.inote.domain.entity.Reply;
import com.pengfu.inote.domain.enums.LikeTypeEnum;
import com.pengfu.inote.domain.vo.common.ResultCode;
import com.pengfu.inote.domain.vo.reply.ReplyPageVO;
import com.pengfu.inote.domain.vo.user.UserPageVO;
import com.pengfu.inote.manager.UserManager;
import com.pengfu.inote.mapper.LikeMapper;
import com.pengfu.inote.mapper.ReplyMapper;
import com.pengfu.inote.service.exception.ServiceException;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ReplyService {

    private ReplyMapper replyMapper;
    private LikeMapper likeMapper;

    private UserManager userManager;

    public ReplyPageVO add(ReplyPostDTO replyPostDTO) {
        Long userId = StpUtil.getLoginIdAsLong();

        Reply reply = new Reply();
        BeanUtils.copyProperties(replyPostDTO, reply);
        reply.setFromUserId(userId);
        reply.setDel(false);

        replyMapper.insert(reply);

        ReplyPageVO replyPageVO = new ReplyPageVO();
        BeanUtils.copyProperties(reply, replyPageVO);

        // 回复用户
        replyPageVO.setUser(UserPageVO.build(userManager.getById(reply.getFromUserId())));

        // @ 用户
        replyPageVO.setAtUser(UserPageVO.build(userManager.getById(reply.getToUserId())));

        return replyPageVO;
    }

    public IPage<ReplyPageVO> getByComment(PageDTO pageDTO, Long commentId) {
        Long userId = null;
        if (StpUtil.isLogin()) {
            userId = StpUtil.getLoginIdAsLong();
        }

        IPage<Reply> replyPage = replyMapper.selectPage(new Page<>(pageDTO.getPage(), pageDTO.getSize()),
                new QueryWrapper<Reply>().lambda()
                        .eq(Reply::getCommentId, commentId)
                        .eq(Reply::getDel, false)
                        .orderByDesc(Reply::getCreateTime));

        IPage<ReplyPageVO> replyPageVOPage = new Page<>();
        BeanUtils.copyProperties(replyPage, replyPageVOPage);
        Long finalUserId = userId;
        replyPageVOPage.setRecords(replyPage.getRecords().stream().map(reply -> {
            ReplyPageVO replyPageVO = new ReplyPageVO();
            BeanUtils.copyProperties(reply, replyPageVO);

            // 回复用户
            replyPageVO.setUser(UserPageVO.build(userManager.getById(reply.getFromUserId())));

            // @ 用户
            replyPageVO.setAtUser(UserPageVO.build(userManager.getById(reply.getToUserId())));

            // 判断当前用户是否点赞
            if (finalUserId != null) {
                replyPageVO.setActive(likeMapper.selectCount(new QueryWrapper<Like>().lambda()
                        .eq(Like::getUserId, finalUserId)
                        .eq(Like::getType, LikeTypeEnum.REPLY)
                        .eq(Like::getTargetId, reply.getId())) != 0);
            } else {
                replyPageVO.setActive(false);
            }

            return replyPageVO;
        }).toList());

        return replyPageVOPage;
    }

    public void del(Long id) {
        Reply reply = replyMapper.selectById(id);
        if (reply == null) {
            throw new ServiceException(ResultCode.NOT_FOUND);
        }

        Long userId = StpUtil.getLoginIdAsLong();
        if (!reply.getFromUserId().equals(userId)) {
            throw new ServiceException(ResultCode.FORBIDDEN);
        }

        reply.setDel(true);
        replyMapper.updateById(reply);
    }

}
