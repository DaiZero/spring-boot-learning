package com.dzero.wf.camunda.demo.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Comment;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * TaskHisTests
 *
 * @author DaiZedong
 * @date 2022/3/7 9:41
 */
@Slf4j
@SpringBootTest
public class TaskHisTests {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    public void list() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("user_task_assignee_001");
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskAssignee("001")
                .singleResult();
        taskService.complete(task.getId());
        taskGetComment(processInstance.getRootProcessInstanceId());
    }

    public void taskGetComment(String processInstanceId) {
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();
        List<Map<String, Object>> result = new ArrayList<>(list.size());
        System.out.println(list.size());
        for (HistoricActivityInstance historicActivityInstance : list) {
            Map<String, Object> map = new HashMap<>(5);
            String taskId = historicActivityInstance.getTaskId();
            List<Comment> taskComments = taskService.getTaskComments(taskId);
            System.out.println(taskComments.size());
            map.put("activityName", historicActivityInstance.getActivityName());
            map.put("activityType", matching(historicActivityInstance.getActivityType()));
            map.put("assignee", historicActivityInstance.getAssignee() == null ? "无" : historicActivityInstance.getAssignee());
            map.put("startTime", DateFormatUtils.format(historicActivityInstance.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("endTime", DateFormatUtils.format(historicActivityInstance.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("costTime", getDatePoor(historicActivityInstance.getEndTime(), historicActivityInstance.getStartTime()));

            if (taskComments.size() > 0) {
                map.put("message", taskComments.get(0).getFullMessage());
            } else {
                map.put("message", "无");
            }
            result.add(map);
        }
    }

    private String matching(String ActivityType) {
        String value = "";
        switch (ActivityType) {
            case "startEvent":
                value = "流程开始";
                break;
            case "userTask":
                value = "用户处理";
                break;
            case "noneEndEvent":
                value = "流程结束";
                break;
            default:
                value = "未知节点";
                break;
        }
        return value;
    }

    public String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟" + sec + "秒";
    }
}
