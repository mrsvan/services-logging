package be.vrt.services.logging.log.consumer.dto;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonLogWrapperDto {

	private Date date;
	private Date logDate;
	private String transactionId;
	private String hostName;
	private String flowId;
	private String className;
	private String methodName;
	private int breadCrum;
	private int lineNumber;
	
	private String logLevel;
	
	private String loggerName;
	private String logComment;
	
	private List<String> ids = new LinkedList<>();
	private Map<String, Object> content = new HashMap<>();
	private Object environmentInfo;

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public Map<String, Object> getContent() {
		return content;
	}

	public void setContent(Map<String, Object> content) {
		this.content = content;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Object getEnvironmentInfo() {
		return environmentInfo;
	}

	public void setEnvironmentInfo(Object environmentInfo) {
		this.environmentInfo = environmentInfo;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public String getLogComment() {
		return logComment;
	}

	public void setLogComment(String logComment) {
		this.logComment = logComment;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public int getBreadCrum() {
		return breadCrum;
	}

	public void setBreadCrum(int breadCrum) {
		this.breadCrum = breadCrum;
	}

	public Date getLogDate() {
		return logDate;
	}

	public void setLogDate(Date logDate) {
		this.logDate = logDate;
	}
}
