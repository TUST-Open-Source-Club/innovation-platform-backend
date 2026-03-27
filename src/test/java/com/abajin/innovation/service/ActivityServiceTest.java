package com.abajin.innovation.service;

import com.abajin.innovation.common.Constants;
import com.abajin.innovation.dto.ActivityDTO;
import com.abajin.innovation.entity.Activity;
import com.abajin.innovation.entity.ActivityRegistration;
import com.abajin.innovation.entity.ActivitySummary;
import com.abajin.innovation.entity.User;
import com.abajin.innovation.enums.ActivityStatus;
import com.abajin.innovation.enums.ApprovalStatus;
import com.abajin.innovation.entity.ActivityType;
import com.abajin.innovation.mapper.ActivityMapper;
import com.abajin.innovation.mapper.ActivityRegistrationMapper;
import com.abajin.innovation.mapper.ActivitySummaryMapper;
import com.abajin.innovation.mapper.ActivityTypeMapper;
import com.abajin.innovation.mapper.SpaceReservationMapper;
import com.abajin.innovation.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.excel.EasyExcel;
import com.abajin.innovation.dto.ActivityImportDTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 活动服务测试
 */
@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityMapper activityMapper;

    @Mock
    private ActivityRegistrationMapper registrationMapper;

    @Mock
    private ActivitySummaryMapper summaryMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SpaceReservationMapper spaceReservationMapper;

    @Mock
    private ActivityTypeMapper activityTypeMapper;

    @InjectMocks
    private ActivityService activityService;

    private User organizer;
    private ActivityDTO activityDTO;
    private Activity activity;

    @BeforeEach
    void setUp() {
        organizer = new User();
        organizer.setId(1L);
        organizer.setUsername("organizer");
        organizer.setRealName("活动组织者");
        organizer.setRole(Constants.ROLE_STUDENT);

        activityDTO = new ActivityDTO();
        activityDTO.setTitle("测试活动");
        activityDTO.setDescription("这是一个测试活动");
        activityDTO.setActivityTypeId(1L);
        activityDTO.setLocation("会议室A");
        activityDTO.setStartTime(LocalDateTime.now().plusDays(1));
        activityDTO.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        activityDTO.setMaxParticipants(100);

        activity = new Activity();
        activity.setId(1L);
        activity.setTitle("测试活动");
        activity.setDescription("这是一个测试活动");
        activity.setOrganizerId(1L);
        activity.setOrganizerName("活动组织者");
        activity.setStatus(ActivityStatus.DRAFT.name());
        activity.setApprovalStatus(ApprovalStatus.PENDING.name());
    }

    @Test
    void createActivity_withValidData_returnsActivity() {
        // Arrange
        when(userMapper.selectById(1L)).thenReturn(organizer);
        when(activityMapper.insert(any(Activity.class))).thenReturn(1);

        // Act
        Activity result = activityService.createActivity(activityDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("测试活动", result.getTitle());
        assertEquals(1L, result.getOrganizerId());
        assertEquals("活动组织者", result.getOrganizerName());
        assertEquals(ActivityStatus.DRAFT.name(), result.getStatus());
        verify(activityMapper).insert(any(Activity.class));
    }

    @Test
    void createActivity_withNonExistentOrganizer_throwsException() {
        // Arrange
        when(userMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.createActivity(activityDTO, 999L));
        assertEquals("组织者不存在", exception.getMessage());
    }

    @Test
    void updateActivity_withValidData_returnsUpdatedActivity() {
        // Arrange
        when(activityMapper.selectById(1L)).thenReturn(activity);
        when(activityMapper.update(any(Activity.class))).thenReturn(1);

        ActivityDTO updateDTO = new ActivityDTO();
        updateDTO.setTitle("更新后的标题");

        // Act
        Activity result = activityService.updateActivity(1L, updateDTO, 1L);

        // Assert
        verify(activityMapper).update(any(Activity.class));
    }

    @Test
    void updateActivity_withNonExistentActivity_throwsException() {
        // Arrange
        when(activityMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.updateActivity(999L, activityDTO, 1L));
        assertEquals("活动不存在", exception.getMessage());
    }

    @Test
    void updateActivity_withUnauthorizedUser_throwsException() {
        // Arrange
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setRole(Constants.ROLE_STUDENT);

        when(activityMapper.selectById(1L)).thenReturn(activity);
        when(userMapper.selectById(2L)).thenReturn(otherUser);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.updateActivity(1L, activityDTO, 2L));
        assertEquals("无权修改此活动", exception.getMessage());
    }

    @Test
    void submitActivity_withDraftStatus_submitsSuccessfully() {
        // Arrange
        when(activityMapper.selectById(1L)).thenReturn(activity);
        when(activityMapper.update(any(Activity.class))).thenReturn(1);

        // Act
        Activity result = activityService.submitActivity(1L, 1L);

        // Assert
        assertEquals(ActivityStatus.SUBMITTED.name(), result.getStatus());
        assertEquals(ApprovalStatus.PENDING.name(), result.getApprovalStatus());
    }

    @Test
    void submitActivity_withNonDraftStatus_throwsException() {
        // Arrange
        activity.setStatus(ActivityStatus.SUBMITTED.name());
        when(activityMapper.selectById(1L)).thenReturn(activity);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.submitActivity(1L, 1L));
        assertEquals("只能提交草稿状态的活动", exception.getMessage());
    }

    @Test
    void submitActivity_withUnauthorizedUser_throwsException() {
        // Arrange
        when(activityMapper.selectById(1L)).thenReturn(activity);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.submitActivity(1L, 999L));
        assertEquals("无权提交此活动", exception.getMessage());
    }

    @Test
    void collegeReview_withValidActivity_approvesActivity() {
        // Arrange
        activity.setStatus(ActivityStatus.SUBMITTED.name());
        when(activityMapper.selectById(1L)).thenReturn(activity);
        when(activityMapper.update(any(Activity.class))).thenReturn(1);

        // Act
        Activity result = activityService.collegeReview(1L, ApprovalStatus.APPROVED.name(), "通过", 2L);

        // Assert
        assertEquals(ActivityStatus.APPROVED.name(), result.getStatus());
        assertEquals(ApprovalStatus.PENDING.name(), result.getApprovalStatus());
        assertEquals("通过", result.getReviewComment());
    }

    @Test
    void collegeReview_withValidActivity_rejectsActivity() {
        // Arrange
        activity.setStatus(ActivityStatus.SUBMITTED.name());
        when(activityMapper.selectById(1L)).thenReturn(activity);
        when(activityMapper.update(any(Activity.class))).thenReturn(1);

        // Act
        Activity result = activityService.collegeReview(1L, ApprovalStatus.REJECTED.name(), "不符合要求", 2L);

        // Assert
        assertEquals(ActivityStatus.REJECTED.name(), result.getStatus());
        assertEquals(ApprovalStatus.REJECTED.name(), result.getApprovalStatus());
    }

    @Test
    void schoolReviewAndPublish_withApprovedActivity_publishesActivity() {
        // Arrange
        activity.setStatus(ActivityStatus.APPROVED.name());
        when(activityMapper.selectById(1L)).thenReturn(activity);
        when(activityMapper.update(any(Activity.class))).thenReturn(1);

        // Act
        Activity result = activityService.schoolReviewAndPublish(1L, ApprovalStatus.APPROVED.name(), "终审通过", 3L);

        // Assert
        assertEquals(ActivityStatus.PUBLISHED.name(), result.getStatus());
        assertEquals(ApprovalStatus.APPROVED.name(), result.getApprovalStatus());
    }

    @Test
    void registerActivity_withPublishedActivity_registersSuccessfully() {
        // Arrange
        activity.setStatus(ActivityStatus.PUBLISHED.name());
        activity.setMaxParticipants(100);
        when(activityMapper.selectById(1L)).thenReturn(activity);
        when(registrationMapper.selectByActivityIdAndUserId(1L, 2L)).thenReturn(null);
        when(registrationMapper.selectByActivityId(1L)).thenReturn(Collections.emptyList());
        when(userMapper.selectById(2L)).thenReturn(organizer);
        when(registrationMapper.insert(any(ActivityRegistration.class))).thenReturn(1);

        // Act
        ActivityRegistration result = activityService.registerActivity(1L, 2L, "13800138000", "test@example.com", "备注");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getActivityId());
        assertEquals(2L, result.getUserId());
        assertEquals(ApprovalStatus.APPROVED.name(), result.getApprovalStatus());
    }

    @Test
    void registerActivity_withNonPublishedActivity_throwsException() {
        // Arrange
        activity.setStatus(ActivityStatus.DRAFT.name());
        when(activityMapper.selectById(1L)).thenReturn(activity);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.registerActivity(1L, 2L, null, null, null));
        assertEquals("活动未发布，不能报名", exception.getMessage());
    }

    @Test
    void registerActivity_withExistingRegistration_throwsException() {
        // Arrange
        activity.setStatus(ActivityStatus.PUBLISHED.name());
        ActivityRegistration existing = new ActivityRegistration();
        when(activityMapper.selectById(1L)).thenReturn(activity);
        when(registrationMapper.selectByActivityIdAndUserId(1L, 2L)).thenReturn(existing);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.registerActivity(1L, 2L, null, null, null));
        assertEquals("您已报名此活动", exception.getMessage());
    }

    @Test
    void getActivityById_withExistingActivity_returnsActivity() {
        // Arrange
        when(activityMapper.selectById(1L)).thenReturn(activity);

        // Act
        Activity result = activityService.getActivityById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("测试活动", result.getTitle());
    }

    @Test
    void getActivities_withPagination_returnsActivities() {
        // Arrange
        List<Activity> activities = Collections.singletonList(activity);
        when(activityMapper.selectPage(0, 10, null, null, null, null)).thenReturn(activities);

        // Act
        List<Activity> result = activityService.getActivities(1, 10, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void countActivities_returnsCount() {
        // Arrange
        when(activityMapper.count(null, null, null, null)).thenReturn(50);

        // Act
        int count = activityService.countActivities(null, null, null, null);

        // Assert
        assertEquals(50, count);
    }

    @Test
    void cancelRegistration_withValidRegistration_cancelsSuccessfully() {
        // Arrange
        ActivityRegistration registration = new ActivityRegistration();
        registration.setId(1L);
        registration.setActivityId(1L);
        registration.setUserId(2L);
        registration.setStatus("ACTIVE");
        when(registrationMapper.selectById(1L)).thenReturn(registration);
        when(registrationMapper.update(any(ActivityRegistration.class))).thenReturn(1);

        // Act
        activityService.cancelRegistration(1L, 2L);

        // Assert
        verify(registrationMapper).update(any(ActivityRegistration.class));
    }

    @Test
    void cancelRegistration_withNonExistentRegistration_throwsException() {
        // Arrange
        when(registrationMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.cancelRegistration(999L, 2L));
        assertEquals("报名记录不存在", exception.getMessage());
    }

    @Test
    void cancelRegistration_withUnauthorizedUser_throwsException() {
        // Arrange
        ActivityRegistration registration = new ActivityRegistration();
        registration.setId(1L);
        registration.setUserId(3L);
        when(registrationMapper.selectById(1L)).thenReturn(registration);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.cancelRegistration(1L, 2L));
        assertEquals("无权取消此报名", exception.getMessage());
    }

    @Test
    void submitSummary_withValidData_submitsSuccessfully() {
        // Arrange
        activity.setOrganizerId(1L);
        when(activityMapper.selectById(1L)).thenReturn(activity);
        when(summaryMapper.selectByActivityId(1L)).thenReturn(null);
        when(summaryMapper.insert(any(ActivitySummary.class))).thenReturn(1);

        ActivitySummary summary = new ActivitySummary();
        summary.setSummaryContent("活动总结内容");

        // Act
        ActivitySummary result = activityService.submitSummary(1L, summary, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(ApprovalStatus.PENDING.name(), result.getApprovalStatus());
    }

    @Test
    void submitSummary_withUnauthorizedUser_throwsException() {
        // Arrange
        activity.setOrganizerId(1L);
        when(activityMapper.selectById(1L)).thenReturn(activity);

        ActivitySummary summary = new ActivitySummary();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.submitSummary(1L, summary, 999L));
        assertEquals("只有活动组织者可以提交总结", exception.getMessage());
    }

    @Test
    void reviewSummary_withValidSummary_approvesSuccessfully() {
        // Arrange
        ActivitySummary summary = new ActivitySummary();
        summary.setId(1L);
        summary.setApprovalStatus(ApprovalStatus.PENDING.name());
        when(summaryMapper.selectById(1L)).thenReturn(summary);
        when(summaryMapper.update(any(ActivitySummary.class))).thenReturn(1);

        // Act
        ActivitySummary result = activityService.reviewSummary(1L, ApprovalStatus.APPROVED.name(), "总结很好", 2L);

        // Assert
        assertEquals(ApprovalStatus.APPROVED.name(), result.getApprovalStatus());
        assertEquals("总结很好", result.getReviewComment());
    }

    @Test
    void reviewSummary_withNonPendingSummary_throwsException() {
        // Arrange
        ActivitySummary summary = new ActivitySummary();
        summary.setId(1L);
        summary.setApprovalStatus(ApprovalStatus.APPROVED.name());
        when(summaryMapper.selectById(1L)).thenReturn(summary);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.reviewSummary(1L, ApprovalStatus.APPROVED.name(), "", 2L));
        assertEquals("该总结已审批，无法重复操作", exception.getMessage());
    }

    // ==================== Excel导入测试 ====================

    @Test
    void importActivitiesFromExcel_withNonExistentOrganizer_throwsException() {
        // Arrange
        when(userMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> activityService.importActivitiesFromExcel(new ByteArrayInputStream(new byte[0]), 999L));
        assertEquals("组织者不存在", exception.getMessage());
    }

    @Test
    void importActivitiesFromExcel_withValidData_importsSuccessfully() throws Exception {
        // Arrange
        when(userMapper.selectById(1L)).thenReturn(organizer);
        when(activityTypeMapper.selectAll()).thenReturn(Collections.emptyList());
        when(activityMapper.insert(any(Activity.class))).thenReturn(1);

        // 创建测试Excel数据
        ActivityImportDTO dto = new ActivityImportDTO();
        dto.setTitle("测试导入活动");
        dto.setActivityType("讲座");
        dto.setStartTime("2026-01-01 10:00:00");
        dto.setEndTime("2026-01-01 12:00:00");
        dto.setLocation("会议室A");
        dto.setDescription("测试描述");
        dto.setContent("测试内容");
        dto.setMaxParticipants(100);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, ActivityImportDTO.class).sheet("活动").doWrite(Collections.singletonList(dto));
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Act
        int count = activityService.importActivitiesFromExcel(inputStream, 1L);

        // Assert
        assertEquals(1, count);
        verify(activityMapper, times(1)).insert(any(Activity.class));
    }

    @Test
    void importActivitiesFromExcel_withMultipleRows_importsAll() throws Exception {
        // Arrange
        when(userMapper.selectById(1L)).thenReturn(organizer);
        when(activityTypeMapper.selectAll()).thenReturn(Collections.emptyList());
        when(activityMapper.insert(any(Activity.class))).thenReturn(1);

        // 创建多个测试数据
        ActivityImportDTO dto1 = new ActivityImportDTO();
        dto1.setTitle("活动1");
        dto1.setStartTime("2026-01-01 10:00");
        dto1.setEndTime("2026-01-01 12:00");

        ActivityImportDTO dto2 = new ActivityImportDTO();
        dto2.setTitle("活动2");
        dto2.setStartTime("2026-01-02 14:00");
        dto2.setEndTime("2026-01-02 16:00");

        List<ActivityImportDTO> list = new ArrayList<>();
        list.add(dto1);
        list.add(dto2);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, ActivityImportDTO.class).sheet("活动").doWrite(list);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Act
        int count = activityService.importActivitiesFromExcel(inputStream, 1L);

        // Assert
        assertEquals(2, count);
        verify(activityMapper, times(2)).insert(any(Activity.class));
    }

    @Test
    void importActivitiesFromExcel_withActivityType_mappingWorks() throws Exception {
        // Arrange
        ActivityType type = new ActivityType();
        type.setId(1L);
        type.setName("创新创业讲座");

        when(userMapper.selectById(1L)).thenReturn(organizer);
        when(activityTypeMapper.selectAll()).thenReturn(Collections.singletonList(type));
        when(activityMapper.insert(any(Activity.class))).thenReturn(1);

        ActivityImportDTO dto = new ActivityImportDTO();
        dto.setTitle("带类型的活动");
        dto.setActivityType("创新创业讲座");
        dto.setStartTime("2026-01-01 10:00:00");
        dto.setEndTime("2026-01-01 12:00:00");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, ActivityImportDTO.class).sheet("活动").doWrite(Collections.singletonList(dto));
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Act
        int count = activityService.importActivitiesFromExcel(inputStream, 1L);

        // Assert
        assertEquals(1, count);
        verify(activityMapper).insert(argThat(activity -> 
            activity.getActivityTypeId() != null && activity.getActivityTypeId().equals(1L)
        ));
    }
}
