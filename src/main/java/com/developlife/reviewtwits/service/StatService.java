package com.developlife.reviewtwits.service;

import com.developlife.reviewtwits.entity.Product;
import com.developlife.reviewtwits.entity.Project;
import com.developlife.reviewtwits.entity.StatInfo;
import com.developlife.reviewtwits.entity.User;
import com.developlife.reviewtwits.exception.product.ProductNotRegisteredException;
import com.developlife.reviewtwits.exception.project.ProjectNotFoundException;
import com.developlife.reviewtwits.exception.user.AccessDeniedException;
import com.developlife.reviewtwits.mapper.StatMapper;
import com.developlife.reviewtwits.message.request.StatMessageRequest;
import com.developlife.reviewtwits.message.response.statistics.DailyVisitInfoResponse;
import com.developlife.reviewtwits.message.response.project.RecentVisitInfoResponse;
import com.developlife.reviewtwits.message.response.statistics.VisitInfoResponse;
import com.developlife.reviewtwits.message.response.statistics.VisitTotalGraphResponse;
import com.developlife.reviewtwits.message.response.project.*;
import com.developlife.reviewtwits.message.response.statistics.SaveStatResponse;
import com.developlife.reviewtwits.message.response.statistics.SimpleProjectInfoResponse;
import com.developlife.reviewtwits.repository.ProductRepository;
import com.developlife.reviewtwits.repository.ProjectRepository;
import com.developlife.reviewtwits.repository.statistics.StatInfoRepository;
import com.developlife.reviewtwits.type.ChartPeriodUnit;
import com.developlife.reviewtwits.type.project.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author WhalesBob
 * @since 2023-04-23
 */
@Service
@RequiredArgsConstructor
public class StatService {

    private final StatInfoRepository statInfoRepository;
    private final ProductRepository productRepository;
    private final ProjectRepository projectRepository;
    private final StatMapper statMapper;

    public SaveStatResponse saveStatInfo(User user, StatMessageRequest statMessageRequest) {

        Device device = Device.valueOf(statMessageRequest.device());

        Product foundProduct = productRepository.findProductByProductUrl(statMessageRequest.productUrl())
                .orElseThrow(() -> new ProductNotRegisteredException("해당 상품이 존재하지 않습니다."));

        StatInfo statInfo = StatInfo.builder()
                .user(user)
                .device(device)
                .inflowUrl(statMessageRequest.inflowUrl())
                .productUrl(statMessageRequest.productUrl())
                .product(foundProduct)
                .project(foundProduct.getProject())
                .build();

        StatInfo savedInfo = statInfoRepository.save(statInfo);
        return statMapper.mapStatInfoToSaveStatResponse(savedInfo);
    }

    public VisitTotalGraphResponse getVisitGraphInfos(String projectName, String inputRange, String inputInterval, User user, String inputEndDate) {
        Project project = getProject(projectName, user);
        ChartPeriodUnit range = ChartPeriodUnit.findByInputValue(inputRange);
        ChartPeriodUnit interval = ChartPeriodUnit.findByInputValue(inputInterval);
        LocalDate endDate = getLocalDateFromInput(inputEndDate);

        RecentVisitInfoResponse recentInfo = statInfoRepository.findRecentVisitInfo(project);
        List<VisitInfoResponse> visitInfo = statInfoRepository.findByPeriod(project, endDate, range, interval);

        return VisitTotalGraphResponse.builder()
                .range(inputRange)
                .interval(inputInterval)
                .todayVisit(recentInfo.todayVisit())
                .yesterdayVisit(recentInfo.yesterdayVisit())
                .totalVisit(recentInfo.totalVisit())
                .visitInfo(visitInfo)
                .build();
    }

    public DailyVisitInfoResponse getDailyVisitInfos(String projectName, String inputRange, User user) {

        Project project = getProject(projectName, user);
        ChartPeriodUnit range = ChartPeriodUnit.findByInputValue(inputRange);

        List<VisitInfoResponse> visitInfo = statInfoRepository.findByPeriod(project, LocalDate.now(), range, ChartPeriodUnit.ONE_DAY);

        return DailyVisitInfoResponse.builder()
                .range(inputRange)
                .visitInfo(visitInfo)
                .build();
    }
    public RecentVisitInfoResponse getRecentVisitCounts(String projectName, User user) {
        Project project = getProject(projectName, user);
        return statInfoRepository.findRecentVisitInfo(project);
    }

    public SimpleProjectInfoResponse getSimpleProjectInfo(String projectName, User user) {
        Project project = getProject(projectName, user);
        return statInfoRepository.findSimpleProjectInfo(project);
    }

    public List<ProductStatisticsResponse> getProductStatisticsInfo(String projectName, User user) {
        Project project = getProject(projectName, user);
        return statInfoRepository.findProductStatistics(project);
    }

    public SearchFlowResponse getRequestSearchFlowInfos(String projectName, User user) {
        Project project = getProject(projectName, user);
        return statInfoRepository.findSearchFlow(project);
    }

    public Map<Integer, Long> getReadTimeInfo(String projectName, User user) {
        Project project = getProject(projectName, user);
        return statInfoRepository.readTimeGraphInfo(project);
    }

    private Project getProject(String projectName, User user) {
        Project project = projectRepository.findByProjectName(projectName)
                .orElseThrow(() -> new ProjectNotFoundException("해당 프로젝트가 존재하지 않습니다."));

        if (!project.getUser().getAccountId().equals(user.getAccountId())) {
            throw new AccessDeniedException("해당 프로젝트 통계정보에 접근할 수 있는 권한이 없습니다.");
        }
        return project;
    }

    private LocalDate getLocalDateFromInput(String inputDate) {
        if (inputDate == null){
            return LocalDate.now();
        }
        return LocalDate.parse(inputDate);
    }
}