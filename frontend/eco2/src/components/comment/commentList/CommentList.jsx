import React, { useEffect } from "react";
import { useSelector } from "react-redux";
import CommentItem from "../commentItem/CommentItem";
import styles from "./CommentList.module.css";

const CommentList = ({ comments, replys, setTest }) => {
  return (
    <ul className={styles.ul}>
      {comments?.length > 0 &&
        comments.map((comment) => (
          <CommentItem
            key={comment.id}
            id={comment.id}
            content={comment.content}
            user={comment.userName}
            commentUserId={comment.userId}
            postId={comment.postId}
            commentId={comment.commentId}
            replys={replys}
            setTest={setTest}
            userEmail={comment.userEmail}
          />
        ))}
    </ul>
  );
};

export default CommentList;
