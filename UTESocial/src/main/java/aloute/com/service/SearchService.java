package aloute.com.service;
import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.UserRepository;
import aloute.com.repository.common.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap; // Dùng LinkedHashMap để giữ thứ tự các trường
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
@Service
public class SearchService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostsRepository postsRepository;

    public List<User> searchUser(String keyword)
    {
    	return userRepository.searchByFullNameOrNameUser(keyword);
    }
    public List<Posts> searchPost(String keyword)
    {
    	return postsRepository.searchPublicPostsByContent(keyword);
    }
//    @Transactional(readOnly = true)
//    public List<Map<String, Object>> searchAll(String keyword) {
//        
//        List<User> foundUsers = userRepository.searchByFullNameOrEmail(keyword);
//        List<Posts> foundPosts = postsRepository.searchPublicPostsByContent(keyword);
//
//        List<Map<String, Object>> results = new ArrayList<>();
//
//        // 2. Chuyển đổi User thành Map
//        for (User user : foundUsers) {
//            Map<String, Object> userMap = new LinkedHashMap<>();
//            userMap.put("type", "USER");
//            userMap.put("id", user.getUserId());
//            userMap.put("title", user.getFullName());
//            userMap.put("subtitle", user.getEmail());
//            userMap.put("avatarUrl", user.getAvatar());
//            userMap.put("content", null); // User không có content
//            userMap.put("createdAt", user.getCreatedAt());
//            results.add(userMap);
//        }
//
//        // 3. Chuyển đổi Post thành Map
//        for (Posts post : foundPosts) {
//            Map<String, Object> postMap = new LinkedHashMap<>();
//            postMap.put("type", "POST");
//            postMap.put("id", post.getPostId());
//            postMap.put("title", post.getUser().getFullName()); // Tên người đăng
//            postMap.put("subtitle", "Bài viết");
//            postMap.put("avatarUrl", post.getUser().getAvatar()); // Avatar người đăng
//            postMap.put("content", post.getContent());
//            postMap.put("createdAt", post.getCreatedAt());
//            results.add(postMap);
//        }
//
//        // 4. Sắp xếp kết quả (tùy chọn)
//        results.sort((map1, map2) -> {
//            LocalDateTime time1 = (LocalDateTime) map1.get("createdAt");
//            LocalDateTime time2 = (LocalDateTime) map2.get("createdAt");
//
//            // Xử lý trường hợp null để tránh lỗi
//            if (time1 == null && time2 == null) return 0; // Cả hai đều null, coi như bằng nhau
//            if (time1 == null) return 1;                  // time1 là null, coi như cũ hơn, đẩy xuống cuối
//            if (time2 == null) return -1;                 // time2 là null, coi như cũ hơn, đẩy xuống cuối
//
//            // Nếu cả hai đều không null, so sánh bình thường (mới nhất lên đầu)
//            return time2.compareTo(time1);
//        });
//
//        return results;
//    }
}
