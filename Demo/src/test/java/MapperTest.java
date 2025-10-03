import com.autoMapper.Application;
import com.autoMapper.entity.po.UserInfo;
import com.autoMapper.entity.query.UserInfoQuery;
import com.autoMapper.mapper.UserInfoMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest( classes = Application.class)
@ExtendWith(SpringExtension.class)
public class MapperTest {

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Test
    public void selectList() {
//        List<UserInfo> dataList = userInfoMapper.selectList(new UserInfoQuery());
//        for (UserInfo data : dataList) {
//            System.out.println(data);
//        }

        UserInfoQuery query = new UserInfoQuery();
        query.setUserId("1");
        query.setCreateTimeStart("2024-01-01");
        List<UserInfo> dataList2 = userInfoMapper.selectList(query);
        for (UserInfo data : dataList2) {
            System.out.println(data);
        }
    }

    @Test
    public void selectCount() {
        int count = userInfoMapper.selectCount(new UserInfoQuery());
        System.out.println(count);
    }

    @Test
    public void update() {
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName("张w");
        userInfo.setCreateTime(new Date());
        userInfoMapper.updateByUserId(userInfo, "1");
    }

    @Test
    public void delete() {
        userInfoMapper.deleteByUserId("12");
    }

    @Test
    public void insert() {
        UserInfo userInfo = new UserInfo();
        UserInfo userInfo1 = new UserInfo();
        userInfo.setUserId("12");
        userInfo.setNickName("张三");
        userInfo.setCreateTime(new Date());

        userInfo1.setUserId("13");
        userInfo1.setNickName("李四");
        userInfo1.setCreateTime(new Date());

        List<UserInfo> userInfoList = new ArrayList<>();
        userInfoList.add(userInfo);
        userInfoList.add(userInfo1);
        userInfoMapper.insertBatch(userInfoList);
    }

}
