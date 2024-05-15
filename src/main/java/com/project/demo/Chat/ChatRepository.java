package com.project.demo.Chat;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.demo.User.User;

public interface ChatRepository extends JpaRepository<Chat,Long> {
    

    public List<Chat> findByUsersId(Long userId);


    @Query("select c from Chat c where :user Member of c.users And :reqUser Member of users")
    public Chat findChatByUsersId(@Param("user") User user,@Param("reqUser") User user2);
    
//  @Query("SELECT c FROM Chat c JOIN c.users u WHERE u = :user1 AND u = :user2")
//      public Chat findChatByUsersId(User user1,User user2);

    //  List<Chat> findByUsersIn(List<User> users);
}
