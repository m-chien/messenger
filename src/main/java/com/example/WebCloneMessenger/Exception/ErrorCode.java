package com.example.WebCloneMessenger.Exception;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    // 1001-1099: Authentication errors
    INVALID_CREDENTIALS(1001, "Email hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1002, "Chưa đăng nhập, vui lòng đăng nhập", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1003, "Token đã hết hạn, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID(1004, "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING(1005, "Thiếu token xác thực", HttpStatus.UNAUTHORIZED),

    // 1100-1199: Authorization errors
    ACCESS_DENIED(1100, "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),
    ADMIN_ONLY(1101, "Chỉ admin mới có quyền thực hiện", HttpStatus.FORBIDDEN),
    NOT_OWNER(1102, "Bạn không phải chủ sở hữu", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED(1103, "Tài khoản của bạn đã bị khóa", HttpStatus.FORBIDDEN),
    ACCOUNT_INACTIVE(1104, "Tài khoản chưa được kích hoạt", HttpStatus.FORBIDDEN),


    // ==================== USER (2xxx) ====================

    USER_NOT_FOUND(2001, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(2002, "Email đã được sử dụng", HttpStatus.CONFLICT),
    EMAIL_DUPLICATE(2003, "Email đã tồn tại trong hệ thống", HttpStatus.CONFLICT),
    PHONE_DUPLICATE(2004, "Số điện thoại đã tồn tại trong hệ thống", HttpStatus.CONFLICT),
    INVALID_EMAIL_FORMAT(2005, "Email không đúng định dạng", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_FORMAT(2006, "Số điện thoại không đúng định dạng", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT(2007, "Mật khẩu phải có ít nhất 6 ký tự", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(2008, "Mật khẩu xác nhận không khớp", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_INCORRECT(2009, "Mật khẩu cũ không đúng", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_YOURSELF(2010, "Không thể xóa chính bản thân", HttpStatus.BAD_REQUEST),
    USERNAME_ALREADY_EXISTS(2011, "Tên người dùng đã tồn tại", HttpStatus.CONFLICT),


    // ==================== MESSAGE (3xxx) ====================

    MESSAGE_NOT_FOUND(3001, "Không tìm thấy tin nhắn", HttpStatus.NOT_FOUND),
    MESSAGE_EMPTY(3002, "Nội dung tin nhắn không được để trống", HttpStatus.BAD_REQUEST),
    MESSAGE_TOO_LONG(3003, "Tin nhắn quá dài (tối đa 5000 ký tự)", HttpStatus.BAD_REQUEST),
    CANNOT_DELETE_MESSAGE(3004, "Bạn không thể xóa tin nhắn này", HttpStatus.FORBIDDEN),
    CANNOT_EDIT_MESSAGE(3005, "Bạn không thể sửa tin nhắn này", HttpStatus.FORBIDDEN),
    MESSAGE_ALREADY_DELETED(3006, "Tin nhắn đã bị xóa", HttpStatus.BAD_REQUEST),


    // ==================== CHAT ROOM (4xxx) ====================

    CHAT_ROOM_NOT_FOUND(4001, "Không tìm thấy phòng chat", HttpStatus.NOT_FOUND),
    CHAT_ROOM_NAME_REQUIRED(4002, "Tên phòng chat không được để trống", HttpStatus.BAD_REQUEST),
    NOT_MEMBER_OF_CHAT(4003, "Bạn không phải thành viên của phòng chat này", HttpStatus.FORBIDDEN),
    ALREADY_MEMBER(4004, "Người dùng đã là thành viên của phòng chat", HttpStatus.CONFLICT),
    CANNOT_LEAVE_CHAT(4005, "Không thể rời phòng chat (bạn là admin duy nhất)", HttpStatus.BAD_REQUEST),
    CANNOT_REMOVE_ADMIN(4006, "Không thể xóa admin khỏi phòng chat", HttpStatus.BAD_REQUEST),
    CHAT_ROOM_FULL(4007, "Phòng chat đã đầy", HttpStatus.BAD_REQUEST),


    // ==================== FILE/MEDIA (5xxx) ====================

    FILE_NOT_FOUND(5001, "Không tìm thấy file", HttpStatus.NOT_FOUND),
    FILE_TOO_LARGE(5002, "File quá lớn (tối đa 10MB)", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(5003, "Loại file không được hỗ trợ", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED(5004, "Upload file thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_EMPTY(5005, "File rỗng", HttpStatus.BAD_REQUEST),
    IMAGE_INVALID(5006, "Ảnh không hợp lệ", HttpStatus.BAD_REQUEST),


    // ==================== VALIDATION (6xxx) ====================

    INVALID_INPUT(6001, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    FIELD_REQUIRED(6002, "Trường bắt buộc không được để trống", HttpStatus.BAD_REQUEST),
    INVALID_ID(6003, "ID không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_DATE_FORMAT(6004, "Định dạng ngày không đúng", HttpStatus.BAD_REQUEST),
    INVALID_ENUM_VALUE(6005, "Giá trị enum không hợp lệ", HttpStatus.BAD_REQUEST),
    FIELD_TOO_LONG(6006, "Trường vượt quá độ dài cho phép", HttpStatus.BAD_REQUEST),
    FIELD_TOO_SHORT(6007, "Trường quá ngắn", HttpStatus.BAD_REQUEST),
    INVALID_URL(6008, "URL không hợp lệ", HttpStatus.BAD_REQUEST),


    // ==================== FRIEND/CONTACT (7xxx) ====================

    FRIEND_REQUEST_NOT_FOUND(7001, "Không tìm thấy lời mời kết bạn", HttpStatus.NOT_FOUND),
    ALREADY_FRIENDS(7002, "Đã là bạn bè", HttpStatus.CONFLICT),
    FRIEND_REQUEST_ALREADY_SENT(7003, "Đã gửi lời mời kết bạn", HttpStatus.CONFLICT),
    CANNOT_ADD_YOURSELF(7004, "Không thể kết bạn với chính mình", HttpStatus.BAD_REQUEST),
    NOT_FRIENDS(7005, "Chưa là bạn bè", HttpStatus.BAD_REQUEST),
    FRIEND_REQUEST_ALREADY_ACCEPTED(7006, "Lời mời kết bạn đã được chấp nhận", HttpStatus.BAD_REQUEST),


    // ==================== NOTIFICATION (8xxx) ====================

    NOTIFICATION_NOT_FOUND(8001, "Không tìm thấy thông báo", HttpStatus.NOT_FOUND),
    NOTIFICATION_ALREADY_READ(8002, "Thông báo đã được đọc", HttpStatus.BAD_REQUEST),


    // ==================== DATABASE & SYSTEM (9xxx) ====================

    DATABASE_ERROR(9001, "Lỗi database", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR(9002, "Lỗi hệ thống, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(9003, "Dịch vụ tạm thời không khả dụng", HttpStatus.SERVICE_UNAVAILABLE),
    NETWORK_ERROR(9004, "Lỗi kết nối mạng", HttpStatus.INTERNAL_SERVER_ERROR),
    TRANSACTION_FAILED(9005, "Giao dịch thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATE_KEY(9006, "Dữ liệu bị trùng lặp", HttpStatus.CONFLICT),
    CONSTRAINT_VIOLATION(9007, "Vi phạm ràng buộc dữ liệu", HttpStatus.BAD_REQUEST),


    // ==================== EXTERNAL SERVICE (10xxx) ====================

    GOOGLE_LOGIN_FAILED(10001, "Đăng nhập Google thất bại", HttpStatus.UNAUTHORIZED),
    FACEBOOK_LOGIN_FAILED(10002, "Đăng nhập Facebook thất bại", HttpStatus.UNAUTHORIZED),
    EMAIL_SEND_FAILED(10003, "Gửi email thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    SMS_SEND_FAILED(10004, "Gửi SMS thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    CLOUD_STORAGE_ERROR(10005, "Lỗi lưu trữ đám mây", HttpStatus.INTERNAL_SERVER_ERROR),


    // ==================== RATE LIMIT & SECURITY (11xxx) ====================

    TOO_MANY_REQUESTS(11001, "Quá nhiều yêu cầu, vui lòng thử lại sau", HttpStatus.TOO_MANY_REQUESTS),
    IP_BLOCKED(11002, "IP của bạn đã bị chặn", HttpStatus.FORBIDDEN),
    SUSPICIOUS_ACTIVITY(11003, "Phát hiện hoạt động đáng ngờ", HttpStatus.FORBIDDEN),
    CAPTCHA_REQUIRED(11004, "Vui lòng xác nhận CAPTCHA", HttpStatus.BAD_REQUEST),
    CAPTCHA_INVALID(11005, "CAPTCHA không đúng", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatusCode httpStatuscode;
}

