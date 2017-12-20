package com.workmarket.domains.api.v2.services;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.workmarket.api.helpers.HttpServletRequestAwareService;
import com.workmarket.api.helpers.LinkRouterServiceImpl;
import com.workmarket.api.v2.model.PaginationPage;
import com.workmarket.api.v2.worker.model.WorkersSearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by ianha on 4/10/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkRouterServiceImplTest {
    private static final String BASE_URL = "http://localhost/some/path";
    private static final int PAGE_SIZE = 25;
    private static final String USER_NUMBER = "12345";

    @Mock HttpServletRequestAwareService servletService;
    @InjectMocks LinkRouterServiceImpl router;

    @Mock PaginationPage page;
    @Mock WorkersSearchResult workersSearchResult;

    @Before
    public void setup() {
        when(page.getTotalRecordCount()).thenReturn(0);

        when(servletService.getRequestUrl()).thenReturn(BASE_URL);
        when(servletService.getServerPath()).thenReturn(BASE_URL);
        when(servletService.getQueryString()).thenReturn("");

        when(workersSearchResult.getUserNumber()).thenReturn(USER_NUMBER);
    }

    @Test
    public void shouldReturnEmptyLinksIfNoResults() {
        assertTrue(CollectionUtils.isEmpty(router.buildLinks(page)));
    }

    @Test
    public void shouldReturnOneLinkIfResultSizeIsOne() {
        mockOnePageResult();
        List<Map<String,String>> links = router.buildLinks(page);
        assertTrue(links.size() == 1);
    }

    @Test
    public void shouldReturnCorrectFirstLinkIfResultSizeIsOne() throws Exception {
        mockOnePageResult();
        Map<String,String> link = router.buildLinks(page).get(0);
        URL url = new URL((String) link.get("first"));
        Map<String, String> query = Splitter.on("&").withKeyValueSeparator("=").split(url.getQuery());

        assertEquals("1", query.get("page"));
        assertEquals(PAGE_SIZE + "", query.get("pageSize"));
    }

    @Test
    public void shouldReturnLastLinkIfMoreThanOnePage() throws Exception {
        mockTwoPageResult();
        List<Map<String,String>> links = router.buildLinks(page);

        Map link = getLinkByRel("last", links);
        URL url = new URL((String) link.get("last"));
        Map<String, String> query = Splitter.on("&").withKeyValueSeparator("=").split(url.getQuery());

        assertEquals("2", query.get("page"));
        assertEquals(PAGE_SIZE + "", query.get("pageSize"));
    }

    @Test
    public void shouldReturnNextLinkIfCurrentPageIsNotLastPage() throws Exception {
        mockTwoPageResult();
        List<Map<String,String>> links = router.buildLinks(page);

        Map link = getLinkByRel("next", links);
        URL url = new URL((String) link.get("next"));
        Map<String, String> query = Splitter.on("&").withKeyValueSeparator("=").split(url.getQuery());

        assertEquals("2", query.get("page"));
        assertEquals(PAGE_SIZE + "", query.get("pageSize"));
    }

    @Test
    public void shouldReturnPrevLinkIfCurrentPageIsNotFirstPage() throws Exception {
        mockTwoPageResult();
        when(page.getPage()).thenReturn(2);
        List<Map<String,String>> links = router.buildLinks(page);

        Map link = getLinkByRel("prev", links);
        URL url = new URL((String) link.get("prev"));
        Map<String, String> query = Splitter.on("&").withKeyValueSeparator("=").split(url.getQuery());

        assertEquals("1", query.get("page"));
        assertEquals(PAGE_SIZE + "", query.get("pageSize"));
    }

    @Test
    public void shouldNotReturnPreviousIfCurrentPageIsFirstPage() {
        mockTwoPageResult();
        List<Map<String,String>> links = router.buildLinks(page);

        assertNull(getLinkByRel("prev", links));
    }

    @Test
    public void shouldNotReturnNextPageIfCurrentPageIsLastPage() {
        mockTwoPageResult();
        when(page.getPage()).thenReturn(2);
        List<Map<String,String>> links = router.buildLinks(page);

        assertNull(getLinkByRel("next", links));
    }

    private Map getLinkByRel(final String rel, List<Map<String,String>> links) {
        return Iterables.find(links, new Predicate<Map>() {
            @Override
            public boolean apply(Map link) {
                return link.keySet().contains(rel);
            }
        }, null);
    }

    private void mockTwoPageResult() {
        when(page.getPage()).thenReturn(1);
        when(page.getPageSize()).thenReturn(PAGE_SIZE);
        when(page.getTotalPageCount()).thenReturn(2);
        when(page.getTotalRecordCount()).thenReturn(PAGE_SIZE + 1);
    }

    private void mockOnePageResult() {
        when(page.getPage()).thenReturn(1);
        when(page.getPageSize()).thenReturn(PAGE_SIZE);
        when(page.getTotalPageCount()).thenReturn(1);
        when(page.getTotalRecordCount()).thenReturn(1);
    }
}