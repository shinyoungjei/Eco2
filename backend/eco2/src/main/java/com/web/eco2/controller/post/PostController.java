package com.web.eco2.controller.post;


import com.web.eco2.domain.dto.mission.QuestDto;
import com.web.eco2.domain.dto.post.CommentDto;
import com.web.eco2.domain.dto.post.PostCreateDto;
import com.web.eco2.domain.dto.post.PostListDto;
import com.web.eco2.domain.dto.post.PostUpdateDto;
import com.web.eco2.domain.entity.Item.Item;
import com.web.eco2.domain.entity.UserSetting;
import com.web.eco2.domain.entity.alarm.FirebaseAlarm;
import com.web.eco2.domain.entity.mission.CustomMission;
import com.web.eco2.domain.entity.mission.Mission;
import com.web.eco2.domain.entity.mission.Quest;
import com.web.eco2.domain.entity.post.Comment;
import com.web.eco2.domain.entity.post.Post;
import com.web.eco2.domain.entity.post.PostImg;
import com.web.eco2.domain.entity.post.QuestPost;
import com.web.eco2.domain.entity.user.User;
import com.web.eco2.model.repository.post.PostImgRepository;
import com.web.eco2.model.repository.user.UserSettingRepository;
import com.web.eco2.model.service.FriendService;
import com.web.eco2.model.service.alarm.AlarmService;
import com.web.eco2.model.service.item.ItemService;
import com.web.eco2.model.service.item.StatisticService;
import com.web.eco2.model.service.mission.CustomMissionService;
import com.web.eco2.model.service.mission.MissionService;
import com.web.eco2.model.service.mission.QuestService;
import com.web.eco2.model.service.post.CommentService;
import com.web.eco2.model.service.post.PostLikeService;
import com.web.eco2.model.service.post.PostService;
import com.web.eco2.model.service.user.UserService;
import com.web.eco2.util.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/post")
@Api(tags = {"Post API"})
@Transactional
@Slf4j
public class PostController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private PostImgRepository postImgRepository;

    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private MissionService missionService;

    @Autowired
    private CustomMissionService customMissionService;

    @Autowired
    private QuestService questService;

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private ItemService itemService;

    //????????? ?????? ??????
    @ApiOperation(value = "????????? ?????? ??????", response = Object.class)
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getPostList(@PathVariable("userId") Long userId) {
        try {
            log.info("????????? ?????? ?????? API ??????");
            ArrayList<PostListDto> postListDtos = new ArrayList<>();
            List<Post> postList = postService.getPostList();

            for (Post post : postList) {
                PostListDto postListDto = new PostListDto();
                PostImg postImg = postImgRepository.getById(post.getId());
                String postImgPath = postImg.getSaveFolder() + '/' + postImg.getSaveName();
                User postUser = post.getUser();
                UserSetting userSetting = userSettingRepository.getById(post.getUser().getId());
                if (!userSetting.isPublicFlag()) {
                    if (friendService.getFriends(userId).contains(postUser) || postUser.getId().equals(userId)) {
                        if (post.isPublicFlag() || postUser.getId().equals(userId)) {
                            Mission mission = null;
                            CustomMission customMission = null;
                            QuestDto quest = null;
                            if (post.getMission() != null) {
                                mission = post.getMission();
                            } else if (post.getCustomMission() != null) {
                                customMission = post.getCustomMission();
                            } else if (post instanceof QuestPost) {
                                quest = ((QuestPost) post).getQuest().toDto();
                            }

                            postListDto.setId(post.getId());
                            postListDto.setUserId(post.getUser().getId());
                            postListDto.setUserName(post.getUser().getName());
                            postListDto.setUserEmail(post.getUser().getEmail());
                            postListDto.setContent(post.getContent());
                            postListDto.setRegistTime(post.getRegistTime());
                            postListDto.setPostImgUrl(postImgPath);
                            postListDto.setUserPublicFlag(userSetting.isPublicFlag());
                            postListDto.setPostPublicFlag(post.isPublicFlag());
                            postListDto.setCommentFlag(post.isCommentFlag());
                            postListDto.setMission(mission);
                            postListDto.setCustomMission(customMission);
                            postListDto.setQuest(quest);
                            postListDto.setLikeCount(postLikeService.likeCount(post.getId()));
                            postListDto.setPostLikeUserIds(postLikeService.specificPostLikeUserIdList(post.getId()));
                            postListDtos.add(postListDto);
                        }
                    }
                } else {
                    if (post.isPublicFlag() || postUser.getId().equals(userId)) {
                        Mission mission = null;
                        CustomMission customMission = null;
                        QuestDto quest = null;
                        if (post.getMission() != null) {
                            mission = post.getMission();
                        } else if (post.getCustomMission() != null) {
                            customMission = post.getCustomMission();
                        } else if (post instanceof QuestPost) {
                            quest = ((QuestPost) post).getQuest().toDto();
                        }

                        postListDto.setId(post.getId());
                        postListDto.setUserId(post.getUser().getId());
                        postListDto.setUserName(post.getUser().getName());
                        postListDto.setUserEmail(post.getUser().getEmail());
                        postListDto.setContent(post.getContent());
                        postListDto.setRegistTime(post.getRegistTime());
                        postListDto.setPostImgUrl(postImgPath);
                        postListDto.setUserPublicFlag(userSetting.isPublicFlag());
                        postListDto.setPostPublicFlag(post.isPublicFlag());
                        postListDto.setCommentFlag(post.isCommentFlag());
                        postListDto.setMission(mission);
                        postListDto.setCustomMission(customMission);
                        postListDto.setQuest(quest);
                        postListDto.setLikeCount(postLikeService.likeCount(post.getId()));
                        postListDto.setPostLikeUserIds(postLikeService.specificPostLikeUserIdList(post.getId()));
                        postListDtos.add(postListDto);
                    }
                }
            }
            return ResponseHandler.generateResponse("?????? ???????????? ?????????????????????.", HttpStatus.OK, "postListDtos", postListDtos);
        } catch (Exception e) {
            log.error("????????? ?????? ?????? API ??????", e);
            return ResponseHandler.generateResponse("????????? ?????????????????????.", HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "?????? ????????? ??????", response = Object.class)
    @GetMapping("/{postId}/{userId}")
    public ResponseEntity<Object> getSpecificPost(@PathVariable("postId") Long postId, @PathVariable("userId") Long userId) {
        try {
            log.info("?????? ????????? ?????? API ??????");
            PostListDto postListDto = new PostListDto();
            Post post = postService.getSpecificPost(postId);
            User postUser = post.getUser();
            PostImg postImg = postImgRepository.getById(postId);
            String postImgPath = postImg.getSaveFolder() + '/' + postImg.getSaveName();
            UserSetting userSetting = userSettingRepository.getById(post.getUser().getId());

            if (!userSetting.isPublicFlag()) {
                // API ????????? ?????? user??? ?????? ??? postUser??? ?????????????????????, ????????? ???????????? ?????? user??? ?????? ?????? ????????? ??????!
                if (friendService.getFriends(userId).contains(postUser) || postUser.getId().equals(userId)) {
                    // ?????? ????????? ????????? ??????
                    if (post.isPublicFlag() || postUser.getId().equals(userId)) {
                        Mission mission = null;
                        CustomMission customMission = null;
                        QuestDto quest = null;
                        if (post instanceof QuestPost) {
                            quest = ((QuestPost) post).getQuest().toDto();
                        } else if (post.getMission() != null) {
                            mission = post.getMission();
                        } else if (post.getCustomMission() != null) {
                            customMission = post.getCustomMission();
                        }

                        postListDto.setId(postId);
                        postListDto.setUserId(post.getUser().getId());
                        postListDto.setUserName(post.getUser().getName());
                        postListDto.setUserEmail(post.getUser().getEmail());
                        postListDto.setContent(post.getContent());
                        postListDto.setRegistTime(post.getRegistTime());
                        postListDto.setPostImgUrl(postImgPath);
                        postListDto.setUserPublicFlag(userSetting.isPublicFlag());
                        postListDto.setPostPublicFlag(post.isPublicFlag());
                        postListDto.setCommentFlag(post.isCommentFlag());
                        postListDto.setMission(mission);
                        postListDto.setCustomMission(customMission);
                        postListDto.setQuest(quest);
                        postListDto.setLikeCount(postLikeService.likeCount(postId));
                        postListDto.setPostLikeUserIds(postLikeService.specificPostLikeUserIdList(postId));

                        if (post.isCommentFlag()) {
                            ArrayList<CommentDto> commentDtos = new ArrayList<>();
                            List<Comment> comments = commentService.getComments(postId);
                            if (comments != null) {
                                for (Comment comment : comments) {
                                    CommentDto commentDto = new CommentDto();
                                    commentDto.setId(comment.getId());
                                    commentDto.setContent(comment.getContent());
                                    commentDto.setRegistTime(comment.getRegistTime());
                                    commentDto.setUserId(comment.getUser().getId());
                                    commentDto.setUserName(comment.getUser().getName());
                                    commentDto.setUserEmail(comment.getUser().getEmail());
                                    commentDto.setPostId(comment.getPost().getId());
                                    if (comment.getComment() != null) {
                                        commentDto.setCommentId(comment.getComment().getId());
                                    }
                                    commentDtos.add(commentDto);
                                }
                                postListDto.setComments(commentDtos);
                            }
                        }
                        return ResponseHandler.generateResponse("?????? ???????????? ?????????????????????.", HttpStatus.OK, "post", postListDto);
                    } else {
                        return ResponseHandler.generateResponse("????????? ??????????????????.", HttpStatus.OK);
                    }
                } else {
                    return ResponseHandler.generateResponse("????????? ???????????????.", HttpStatus.OK);
                }
            } else {
                if (post.isPublicFlag() || postUser.getId().equals(userId)) {
                    Mission mission = null;
                    CustomMission customMission = null;
                    QuestDto quest = null;
                    if (post instanceof QuestPost) {
                        quest = ((QuestPost) post).getQuest().toDto();
                    } else if (post.getMission() != null) {
                        mission = post.getMission();
                    } else if (post.getCustomMission() != null) {
                        customMission = post.getCustomMission();
                    }

                    postListDto.setId(postId);
                    postListDto.setUserId(post.getUser().getId());
                    postListDto.setUserName(post.getUser().getName());
                    postListDto.setUserEmail(post.getUser().getEmail());
                    postListDto.setContent(post.getContent());
                    postListDto.setRegistTime(post.getRegistTime());
                    postListDto.setPostImgUrl(postImgPath);
                    postListDto.setUserPublicFlag(userSetting.isPublicFlag());
                    postListDto.setPostPublicFlag(post.isPublicFlag());
                    postListDto.setCommentFlag(post.isCommentFlag());
                    postListDto.setMission(mission);
                    postListDto.setCustomMission(customMission);
                    postListDto.setQuest(quest);
                    postListDto.setLikeCount(postLikeService.likeCount(postId));
                    postListDto.setPostLikeUserIds(postLikeService.specificPostLikeUserIdList(postId));

                    if (post.isCommentFlag()) {
                        ArrayList<CommentDto> commentDtos = new ArrayList<>();
                        List<Comment> comments = commentService.getComments(postId);
                        if (comments != null) {
                            for (Comment comment : comments) {
                                CommentDto commentDto = new CommentDto();
                                commentDto.setId(comment.getId());
                                commentDto.setContent(comment.getContent());
                                commentDto.setRegistTime(comment.getRegistTime());
                                commentDto.setUserId(comment.getUser().getId());
                                commentDto.setUserName(comment.getUser().getName());
                                commentDto.setUserEmail(comment.getUser().getEmail());
                                commentDto.setPostId(comment.getPost().getId());
                                if (comment.getComment() != null) {
                                    commentDto.setCommentId(comment.getComment().getId());
                                }
                                commentDtos.add(commentDto);
                            }
                            postListDto.setComments(commentDtos);
                        }
                    }
                    return ResponseHandler.generateResponse("?????? ???????????? ?????????????????????.", HttpStatus.OK, "post", postListDto);
                } else {
                    return ResponseHandler.generateResponse("????????? ??????????????????.", HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            log.error("?????? ????????? ?????? API ??????", e);
            return ResponseHandler.generateResponse("????????? ?????????????????????.", HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "????????? ??????", response = Object.class)
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> createPost(@RequestPart(value = "postImage") MultipartFile postImage,
                                             @RequestPart(value = "postCreateDto") PostCreateDto postCreateDto) throws IOException {
        try {
            log.info("????????? ?????? API ??????"); //TODO fe: ????????? ??????, ?????? //be: ?????? ??? ??????
            Long userId = postCreateDto.getUser().getId();
            User user = userService.getById(userId);
            if (user == null) {
                return ResponseHandler.generateResponse("???????????? ?????? ???????????????.", HttpStatus.ACCEPTED);
            }

            Integer category = null;
            boolean isQuest = false;

            if (postCreateDto.getMission() != null) {
                Mission mission = missionService.findByMisId(postCreateDto.getMission().getId());
                postCreateDto.getMission().setCategory(mission.getCategory());
                category = mission.getCategory();
            } else if (postCreateDto.getCustomMission() != null) {
                CustomMission mission = customMissionService.findByCumId(postCreateDto.getCustomMission().getId());
                if (mission == null) {
                    return ResponseHandler.generateResponse("???????????? ?????? ????????????????????????.", HttpStatus.ACCEPTED);
                }
                postCreateDto.setCustomMission(mission);
                category = mission.getCategory();
            } else if (postCreateDto.getQuest() != null) {
                Optional<Quest> questOpt = questService.findById(postCreateDto.getQuest().getId());
                if (questOpt.isEmpty()) {
                    return ResponseHandler.generateResponse("???????????? ?????? ??????????????????.", HttpStatus.ACCEPTED);
                }

                Quest quest = questOpt.get();
                category = quest.getMission().getCategory();
                isQuest = true;

                if (postService.existsByUserIdAndQuestId(userId, quest.getId())) {
                    return ResponseHandler.generateResponse("?????? ????????? ??????????????????.", HttpStatus.ACCEPTED);
                }
                if (quest.isFinishFlag()) {
                    return ResponseHandler.generateResponse("????????? ??????????????????.", HttpStatus.ACCEPTED);
                }
                if (quest.getFinishTime().isBefore(LocalDateTime.now())) {
                    quest.setFinishFlag(true);
                    questService.save(quest);
                    return ResponseHandler.generateResponse("????????? ??????????????????.", HttpStatus.ACCEPTED);
                }

                if (quest.isAchieveFlag()) {
                    return ResponseHandler.generateResponse("????????? ??????????????????.", HttpStatus.ACCEPTED);
                }
                int participantCount = quest.getParticipantCount() + 1;
                if (participantCount == quest.getAchieveCount()) {
                    quest.setAchieveFlag(true);
                    Item item = Item.builder().category(7).user(user).left(100).top(50).build();
                    itemService.save(item);
                    alarmService.insertAlarm(FirebaseAlarm.builder().userId(userId)
                            .content(quest.getContent() + " ???????????? ?????????????????????.").dType("questAchieve")
                            .url("/mainTree").build());

                    for (QuestPost questPost : postService.findByQuest(quest)) {
                        User questUser = questPost.getUser();
                        item = Item.builder().category(7).user(questUser).left(200).top(50).build();
                        itemService.save(item);
                        alarmService.insertAlarm(FirebaseAlarm.builder().userId(questUser.getId())
                                .content(quest.getContent() + " ???????????? ?????????????????????. ?????????????????? ????????? ???????????????.").dType("questAchieve")
                                .url("/mainTree").senderId(item.getId()).build());
                    }
                }
                quest.setParticipantCount(participantCount);
                questService.save(quest);
                postCreateDto.setQuest(quest);
            } else {
                return ResponseHandler.generateResponse("???????????? ???????????????.", HttpStatus.ACCEPTED);
            }

            postCreateDto.setUser(user);
            postCreateDto.setRegistTime(LocalDateTime.now());
            postService.savePost(postImage, postCreateDto);
            statisticService.updateCount(userId, category, isQuest);
            Item item = Item.builder().left(50).top(50).category(category).user(user).build();
            itemService.save(item);
            postCreateDto.setItemId(item.getId());

            return ResponseHandler.generateResponse("???????????? ?????????????????????.", HttpStatus.OK, "postCreateDto", postCreateDto);
        } catch (Exception e) {
            log.error("????????? ?????? API ??????", e);
            return ResponseHandler.generateResponse("????????? ?????????????????????.", HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "????????? ??????", response = Object.class)
    @PutMapping(value = "/{postId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> updatePost(@PathVariable("postId") Long postId,
                                             @RequestPart(value = "postImage") MultipartFile postImage,
                                             @RequestPart(value = "postUpdateDto") PostUpdateDto postUpdateDto) {
        try {
            log.info("????????? ?????? API ??????");
            postService.updatePost(postId, postImage, postUpdateDto);
            return ResponseHandler.generateResponse("???????????? ?????????????????????.", HttpStatus.OK);
        } catch (Exception e) {
            log.error("????????? ?????? API ??????", e);
            return ResponseHandler.generateResponse("????????? ?????????????????????.", HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "????????? ??????", response = Object.class)
    @DeleteMapping("/{postId}")
    public ResponseEntity<Object> deletePost(@PathVariable("postId") Long postId) {
        try {
            log.info("????????? ?????? API ??????");
            Post post = postService.getById(postId);
            if (post == null) {
                return ResponseHandler.generateResponse("???????????? ???????????? ????????????.", HttpStatus.ACCEPTED);
            }
            postService.deletePost(postId);
            return ResponseHandler.generateResponse("???????????? ?????????????????????.", HttpStatus.OK);
        } catch (Exception e) {
            log.error("????????? ?????? API ??????", e);
            return ResponseHandler.generateResponse("????????? ?????????????????????.", HttpStatus.BAD_REQUEST);
        }
    }
}
