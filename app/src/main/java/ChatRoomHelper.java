import android.text.TextUtils;

import com.example.a52374.myapplication.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.constant.MemberType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 吴广庆 on 2016/11/17.
 */

public class ChatRoomHelper {
    private static final int[] imageRes = {R.drawable.room_cover_36, R.drawable.room_cover_37, R.drawable.room_cover_49,
            R.drawable.room_cover_50, R.drawable.room_cover_57, R.drawable.room_cover_58, R.drawable.room_cover_64,
            R.drawable.room_cover_72};

    private static Map<String, Integer> roomCoverMap = new HashMap<>();
    private static int index = 0;


    public static void init() {
        getInstance().clear();

        getInstance().registerObservers(true);
    }

    public static void logout() {
        getInstance().registerObservers(false);
        getInstance().clear();
    }

    /**
     * ************************************ 单例 ***************************************
     */
    public static ChatRoomHelper getInstance() {
        return InstanceHolder.instance;
    }
    /**
     * ************************************ 单例 ***************************************
     */
    static class InstanceHolder {
        final static ChatRoomHelper instance = new ChatRoomHelper();
    }








    private Map<String, Map<String, ChatRoomMember>> cache = new HashMap<>();

    private List<RoomMemberChangedObserver> roomMemberChangedObservers = new ArrayList<>();

//    private Map<String, List<SimpleCallback<ChatRoomMember>>> frequencyLimitCache = new HashMap<>(); // 重复请求处理

    public void clear() {
        cache.clear();
//        frequencyLimitCache.clear();
        roomMemberChangedObservers.clear();
    }

    public void clearRoomCache(String roomId) {
        if (cache.containsKey(roomId)) {
            cache.remove(roomId);
        }
    }

    /**
     * ********************************** 监听 ********************************
     */

    public void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }

    private Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {
            if (messages == null || messages.isEmpty()) {
                return;
            }

            for (IMMessage msg : messages) {
                if (msg == null) {

                    continue;
                }

                if (msg.getMsgType() == MsgTypeEnum.notification) {
                    handleNotification(msg);
                }
            }
        }
    };


    private void handleNotification(IMMessage message) {
        if (message.getAttachment() == null) {
            return;
        }

        String roomId = message.getSessionId();
        ChatRoomNotificationAttachment attachment = (ChatRoomNotificationAttachment) message.getAttachment();
        List<String> targets = attachment.getTargets();
        if (targets != null) {
            for (String target : targets) {
                ChatRoomMember member = getChatRoomMember(roomId, target);
                handleMemberChanged(attachment.getType(), member);
            }
        }
    }

    public ChatRoomMember getChatRoomMember(String roomId, String account) {
        if (cache.containsKey(roomId)) {
            return cache.get(roomId).get(account);
        }

        return null;
    }


    private void handleMemberChanged(NotificationType type, ChatRoomMember member) {
        if (member == null) {
            return;
        }

        switch (type) {
            case ChatRoomMemberIn:
                for (RoomMemberChangedObserver o : roomMemberChangedObservers) {
                    o.onRoomMemberIn(member);
                }
                break;
            case ChatRoomMemberExit:
                for (RoomMemberChangedObserver o : roomMemberChangedObservers) {
                    o.onRoomMemberExit(member);
                }
                break;
            case ChatRoomManagerAdd:
                member.setMemberType(MemberType.ADMIN);
                break;
            case ChatRoomManagerRemove:
                member.setMemberType(MemberType.NORMAL);
                break;
            case ChatRoomMemberBlackAdd:
                member.setInBlackList(true);
                break;
            case ChatRoomMemberBlackRemove:
                member.setInBlackList(false);
                break;
            case ChatRoomMemberMuteAdd:
                member.setMuted(true);
                break;
            case ChatRoomMemberMuteRemove:
                member.setMuted(false);
                member.setMemberType(MemberType.GUEST);
                break;
            case ChatRoomCommonAdd:
                member.setMemberType(MemberType.NORMAL);
                break;
            case ChatRoomCommonRemove:
                member.setMemberType(MemberType.GUEST);
                break;
            default:
                break;
        }

        saveMember(member);
    }
    private void saveMember(ChatRoomMember member) {
        if (member != null && !TextUtils.isEmpty(member.getRoomId()) && !TextUtils.isEmpty(member.getAccount())) {
            Map<String, ChatRoomMember> members = cache.get(member.getRoomId());

            if (members == null) {
                members = new HashMap<>();
                cache.put(member.getRoomId(), members);
            }

            members.put(member.getAccount(), member);
        }
    }


    /**
     * ************************** 在线用户变化通知 ****************************
     */

    public interface RoomMemberChangedObserver {
        void onRoomMemberIn(ChatRoomMember member);

        void onRoomMemberExit(ChatRoomMember member);
    }

    public void registerRoomMemberChangedObserver(RoomMemberChangedObserver o, boolean register) {
        if (o == null) {
            return;
        }

        if (register) {
            if (!roomMemberChangedObservers.contains(o)) {
                roomMemberChangedObservers.add(o);
            }
        } else {
            roomMemberChangedObservers.remove(o);
        }
    }
}
