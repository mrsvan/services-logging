package be.vrt.services.log.exposer.controller;

import be.vrt.services.log.exposer.es.ElasticSearchQueryExecutor;
import be.vrt.services.log.exposer.es.ElasticSearchSearchQueryHttpExecutor;
import be.vrt.services.log.exposer.es.query.DailyProblemQuery;
import be.vrt.services.log.exposer.es.query.DetailQuery;
import be.vrt.services.log.exposer.es.query.StatsQuery;
import be.vrt.services.log.exposer.es.result.ElasticSearchCountResult;
import be.vrt.services.logging.log.common.AppWithEnv;
import be.vrt.services.logging.log.common.LoggingProperties;
import be.vrt.services.logging.log.common.transaction.TransactionRegistery;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static be.vrt.services.log.exposer.controller.JsonMap.mapWith;

@SuppressWarnings("serial")
public class TransactionLogController extends HttpServlet {

	private String[] connectionUrls = LoggingProperties.connectionUrls();

	private ElasticSearchQueryExecutor elasticSearchQueryExecutor = new ElasticSearchSearchQueryHttpExecutor();

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		Map map = new HashMap();
		List logs;
		String path = req.getPathInfo() == null ? "/" : req.getPathInfo();
		map.put("request-path", req.getPathInfo());

		logs = TransactionRegistery.list();
		if (path.matches("/[^/]*")) {
			switch (path) {
				case "/error":
					logs = TransactionRegistery.listErrors();
					break;
				case "/fail":
					logs = TransactionRegistery.listFailures();
					break;
				case "/flows":
					logs = TransactionRegistery.listIds();
					break;
				case "/static-flows":
					logs = new LinkedList();
					List flows = new LinkedList();
					for (Map.Entry<String, String> entrySet : TransactionRegistery.listStaticFlows().entrySet()) {
						String key = entrySet.getKey();
						String value = entrySet.getValue();
						Map<String, String> m = new HashMap<>();
						m.put("name", key);
						m.put("id", value);
						flows.add(m);
					}
					map.put("flows", flows);
					break;
			}
			map.put("logs", logs);
		} else if (path.matches("/transaction/[^/]*")) {

			String id = path.substring("/transaction/".length()).trim();
			// Apache replace + by spaces => Known issue
			id = id.replaceAll(" ", "+");

			Map results = elasticSearchQueryExecutor.executeSearchQueryMultiInstances(new DetailQuery(id)).getData();
			map.put("hits", results);
		} else if (path.matches("/stats/overview/[^/]*")) {
			String date = path.substring("/stats/overview/".length()).trim();

			List<AppWithEnv> appWithEnvs = LoggingProperties.statsAppsWithEnv();
			ElasticSearchCountResult result;
			if(appWithEnvs == null || appWithEnvs.isEmpty()) {
				result = elasticSearchQueryExecutor.executeCountQuery(new StatsQuery(date));
			} else {
				result = elasticSearchQueryExecutor.executeCountQuery(new StatsQuery(date, appWithEnvs));
			}

			map.put("agg", result.getAggregations());

		} else if (path.matches("/stats/errors/[^/]*")) {
			String date = path.substring("/stats/errors/".length()).trim();

			Map result = getDailyProblemsWithLevelAndDate(date, "ERROR");

			map.put("statshits", result);
		} else if (path.matches("/stats/failures/[^/]*")) {
			String date = path.substring("/stats/failures/".length()).trim();

			Map result = getDailyProblemsWithLevelAndDate(date, "FAIL");

			map.put("statshits", result);
		}

		map.put("info",
			mapWith("urls", connectionUrls)
				.mapAnd("statUrl", LoggingProperties.connectionStatUrl())
				.mapAnd("statsAppsWithEnv", LoggingProperties.statsAppsWithEnv())
		);

		resp.setContentType("application/json");
		String json = mapper.writeValueAsString(map);
		resp.getWriter().print(json);

	}

	private Map getDailyProblemsWithLevelAndDate(String date, String level) {
		List<AppWithEnv> appWithEnvs = LoggingProperties.statsAppsWithEnv();
		if(appWithEnvs == null || appWithEnvs.isEmpty()) {
			return elasticSearchQueryExecutor.executeSearchQuery(new DailyProblemQuery(date, level)).getData();
		} else {
			return elasticSearchQueryExecutor.executeSearchQuery(new DailyProblemQuery(date, level, appWithEnvs)).getData();
		}
	}

}
