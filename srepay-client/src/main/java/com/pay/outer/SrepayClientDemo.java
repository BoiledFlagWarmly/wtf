package com.pay.outer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class SrepayClientDemo {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final String CHARSET = "UTF-8";

	private final String registUrl = "http://10.10.129.33:8081/srepay-plat-portal/regist";

	public final String bindPayRemitCardUrl = "http://10.10.129.33:8081/srepay-plat-portal/PAY_REMIT/bindCard";

	private final String payUrl = "http://10.10.129.33:8081/srepay-plat-portal/pay/trade";
	private final String remtiUrl = "http://10.10.129.33:8081/srepay-plat-portal/remit/trade";

	public final String queryCardInfoUrl = "http://10.10.129.33:8081/srepay-plat-portal/queryCardInfo";
	public final String queryTransInfoUrl = "http://10.10.129.33:8081/srepay-plat-portal/queryTransInfo";

	private static final String KEY = "CF5E785EA30CBCC2E833DF93F93AB1C0";// 每个服务商对应一个key
																			// 此key对应服务商为8620805971:CF5E785EA30CBCC2E833DF93F93AB1C0

	@Test
	public void testQueryCardInfo() throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put("registId", "101451242193112269011");
		map.put("bankAccountNo", "6216261000000002485");
		map.put("agentNo", "8620805971");
		map.put("sign", generateSign(map));
		/**
		 * status=SRP001 请求成功 status=SRP002 请求重复 status=SRPE999处理异常 status=01签名验证失败
		 * status=99系统异常 若status=SRP001或SRP002，result=null 返回值样例：
		 * {"result":null,"datas":"eyJjYXJkSWQiOiIxMDA3MjEyMjEyNzUwNDk1NzQ2IiwidXNlYWJsZVRyYWRlVHlwZSI6IkFMTCIsImh0bWwiOm51bGx9","sign":
		 * "6879C444F7F9D827741AFCE7FD09B05D","status":"SRP001"} 其中datas解析后的结构为
		 * {"cardId":"1007212212750495746","useableTradeType":"ALL","html":null}
		 */
		String response = sendRequest(map, queryCardInfoUrl);

		Map<String, String> resultMap = JsonUtils.toObject(response, new TypeReference<Map<String, String>>() {
		});
		if (Objects.nonNull(resultMap.get("datas"))) {
			String datas = new String(Base64.decodeBase64(resultMap.get("datas")), CHARSET);
			Map<String, String> dataMaps = JsonUtils.toObject(datas, new TypeReference<Map<String, String>>() {
			});
			logger.info("{}", JsonUtils.toJsonString(dataMaps));
		}
	}

	@Test
	public void queryTransInfoUrl() throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		map.put("requestId", "1019045178585690114");
		map.put("agentNo", "8620805971");
		map.put("sign", generateSign(map));

		/**
		 * status=SRP001 请求成功 status=SRP002 请求重复 status=SRPE999处理异常 status=01签名验证失败
		 * status=99系统异常 返回值样例：
		 * {"result":null,"datas":"eyJ0cmFuc1JlcXVlc3RTdGF0dXMiOiJTVUNDRVNTIiwidHJhbnNJZCI6IjEwMTM5ODU1MDc5MDI1OTkxNjkifQ==","sign":
		 * "65E7B17A802E5FB35CAF21708AFDC775","status":"SRP001"} 其中datas解析后的结构为
		 * {"transRequestStatus":"SUCCESS","transId":"1013985507902599169"}
		 */
		String response = sendRequest(map, queryTransInfoUrl);

		Map<String, String> resultMap = JsonUtils.toObject(response, new TypeReference<Map<String, String>>() {
		});
		if (Objects.nonNull(resultMap.get("datas"))) {
			String datas = new String(Base64.decodeBase64(resultMap.get("datas")), CHARSET);
			Map<String, String> dataMaps = JsonUtils.toObject(datas, new TypeReference<Map<String, String>>() {
			});
			System.out.println(JsonUtils.toJsonString(dataMaps));
		}
	}

	@Test
	public void testRegist() throws Exception {

		Map<String, String> map = new HashMap<>();
		map.put("agentNo", "8620805971");
		map.put("agentOrganizationCode", "29000201");
		map.put("customerNo", "6666");// 需要传入商编
		map.put("phoneNo", "13945789380");
		map.put("identityNo", "120223199109241000");
		map.put("name", "张三");
		map.put("system", "business");
		map.put("oper", "business");
		String sign = generateSign(map);
		map.put("sign", sign);
		System.out.println(sign);

		/**
		 * status=SRP001 请求成功 status=SRP002 请求重复 status=SRPE999处理异常 status=01签名验证失败
		 * status=99系统异常 返回值样例：
		 * {"result":null,"datas":"eyJyZWdpc3RJZCI6IjEwMTQ1MTUwMjEwNDA2NDgxOTQiLCJzdGF0dXMiOiJFTkFCTEUifQ==","sign":"7D6E3F0FFD1A2CE6FB644A0D87EB6348","status":
		 * "SRP001"} 其中datas解析后的结构为 {"registId":"1014515021040648194","status":"ENABLE"}
		 */
		String response = sendRequest(map, registUrl);

		Map<String, String> resultMap = JsonUtils.toObject(response, new TypeReference<Map<String, String>>() {
		});
		if (generateSign(resultMap).equals(resultMap.get("sign"))) {
			System.out.println(JsonUtils.toJsonString(resultMap));
			String datas = new String(Base64.decodeBase64(resultMap.get("datas")), CHARSET);
			Map<String, String> dataMaps = JsonUtils.toObject(datas, new TypeReference<Map<String, String>>() {
			});
			System.out.println(JsonUtils.toJsonString(dataMaps));
		} else {
			logger.error("验签失败...");
		}
	}

	@Test
	public void remotePayRemitCard() throws Exception {

		Map<String, String> map = new HashMap<>();
		map.put("registId", "998808022905405442");
		map.put("agentNo", "8620805971");
		map.put("bankName", "中国农业银行金华市分行");
		map.put("bankCode", "ABC");
		map.put("bankAccountNo", "6216261000000000099");
		map.put("bankAccountName", "张三");
		map.put("phoneNo", "13552535506");
		map.put("identityNo", "230503199412031314");
		map.put("cvn2", ""); // 信用卡cvn2，传入将带入银联页面( 可选 )
		map.put("expired", "");// 信用卡有效日期，传入将带入银联页面( 可选 )
		map.put("deviceID", "deviceID001");
		map.put("deviceType", "PAD");
		map.put("sourceIP", "127.0.0.1");
		map.put("frontNotifyUrl", ""); // 前台回调地址
		map.put("alliedbankCode", "102100002271");
		map.put("ownbankCode", "ICBC");
		map.put("sabkCode", "102100099996");
		map.put("province", "河北省");
		map.put("city", "北京市");
		map.put("cardCategory", "DEBIT");
		map.put("cardBin", "622111");
		map.put("system", "busines");
		map.put("oper", "business");
		map.put("sign", generateSign(map));

		/**
		 * status=SRP001 请求成功 status=SRP002 请求重复 status=SRPE999处理异常 status=01签名验证失败
		 * status=99系统异常 返回值样例： {"result":null,"datas":
		 * "eyJjYXJkSWQiOiIxMDE0NTE3Mjg3Njg4NjY3MTM3IiwidXNlYWJsZVRyYWRlVHlwZSI6bnVsbCwiaHRtbCI6IjxodG1sPjxoZWFkPjxtZXRhIGh0dHAtZXF1aXY9XCJDb250ZW50LVR5cGVcIiBjb250ZW50PVwidGV4dC9odG1sOyBjaGFyc2V0PVVURi04XCIvPjwvaGVhZD48Ym9keT48Zm9ybSBpZCA9IFwicGF5X2Zvcm1cIiBhY3Rpb249XCJodHRwczovL2dhdGV3YXkudGVzdC45NTUxNi5jb20vZ2F0ZXdheS9hcGkvZnJvbnRUcmFuc1JlcS5kb1wiIG1ldGhvZD1cInBvc3RcIj48aW5wdXQgdHlwZT1cImhpZGRlblwiIG5hbWU9XCJiaXpUeXBlXCIgaWQ9XCJiaXpUeXBlXCIgdmFsdWU9XCIwMDA5MDJcIi8+PGlucHV0IHR5cGU9XCJoaWRkZW5cIiBuYW1lPVwidG9rZW5QYXlEYXRhXCIgaWQ9XCJ0b2tlblBheURhdGFcIiB2YWx1ZT1cInt0cklkPTYyMDAwMDAwMDAxJnRva2VuVHlwZT0wMX1cIi8+PGlucHV0IHR5cGU9XCJoaWRkZW5cIiBuYW1lPVwidHhuU3ViVHlwZVwiIGlkPVwidHhuU3ViVHlwZVwiIHZhbHVlPVwiMDBcIi8+PGlucHV0IHR5cGU9XCJoaWRkZW5cIiBuYW1lPVwib3JkZXJJZFwiIGlkPVwib3JkZXJJZFwiIHZhbHVlPVwiQkNXRE1EUkEyUFM0VEsxSkNWWEdcIi8+PGlucHV0IHR5cGU9XCJoaWRkZW5cIiBuYW1lPVwiYmFja1VybFwiIGlkPVwiYmFja1VybFwiIHZhbHVlPVwiaHR0cDovLzYxLjUwLjEzMC4yNDY6NTA1MC9wYXlpbnRlcmZhY2UtZGlzcGF0Y2hlci1mcm9udC9VTklPTlBBWTExMDAwMU5vdGljZS9vcGVuQ2FyZE5vdGlmeVwiLz48aW5wdXQgdHlwZT1cImhpZGRlblwiIG5hbWU9XCJzaWduYXR1cmVcIiBpZD1cInNpZ25hdHVyZVwiIHZhbHVlPVwiWU1oYzJmQXpxcTM5S0RobHF0NFIwekFIZHpqYlhTZUxKSW1iaVRFcWt2aEVJRm01TVRyTU13UjgwcU80MmtkTFhLNWZUTDdPWTVaajhtV2xuZmQxUkdiQTJTUFlzNUJuZ0g1Q0lpTFhGeTRQODhzS0FiOHpNbWl1RHN3cFhOcDlCVk5KY0YzTHh5R0RaeVJqUlFlT0R6QWNrZURNQWpJNlM4MVN4cnZrWDI0SklyRFU4U3VOWTlVMHp5dXk3NjNlV1U5ZFNWYmN5cXJXTjAwK2h5WVJkN20zTi9EUXdVYnFIanBpK0Q4ZGlOK2dBQzJZckVBWmY0cVM0ZU04ZFQwZjJNYVhCN2VQOXlpZmVRbWxRd3B4eFFmVTcxbVAxT1VmdGVFNEMxZzNpK1BrNHBwblVxRUduY09MK1cyTE84WDNUOEZBdEdOVDFxRUdjTDd3TVJxTHJBPT1cIi8+PGlucHV0IHR5cGU9XCJoaWRkZW5cIiBuYW1lPVwiYWNjTm9cIiBpZD1cImFjY05vXCIgdmFsdWU9XCJGb0lNMVpDUFRZMStFS1lzZ0xkKzlib09qSTBWQ25IQzg5VEVwV1FtS2NISFdLem51d0o5K21jdHFtNDlGOCt0anR6N1YxcDJzK3dnNis4a2tzU1BsNmxqQmk0STJUeUFUZmJRbEpid2luYUw2Z1BPUWpEWUsyT1pPWHdsdGMwb1BtT0JlVmdWNWVQWmZBaTBsQWtoWmFxb0RtVWhTdGRPVllUNDFjMmlVdlY2aDFiaWhJVnk4NG9OSmEzYVRIeGhleVNHaitVZGFGbElnSVlxcW4vVDRia1MvYjdVOS9oZEppVkNJSDdjaFlQUzV3cm9laExtSmJMTmNTK3NVR0dHZDBjTVoweUFnenRDRmUxajMwM1doK1BVb3g3ZTQvelRoZkNrL3FNei9iNFFDdCtsbC83K1hJRHYzMEh5MllkT09pVTRtWmN2T0liTG5abnhJOFpTNXc9PVwiLz48aW5wdXQgdHlwZT1cImhpZGRlblwiIG5hbWU9XCJjdXN0b21lckluZm9cIiBpZD1cImN1c3RvbWVySW5mb1wiIHZhbHVlPVwiZTJObGNuUnBaa2xrUFRNME1URXlOakU1Tnpjd09USXhPRE0yTmlaalpYSjBhV1pVY0Qwd01TWmxibU55ZVhCMFpXUkpibVp2UFZkQlRHczNURFl2VDNwUlVGTnNhV0pqTVVGQ1drWk5PWEJKYWpodVFqbDNWakpJUVVwNmNIVkVVMnhHZWtacU5FOXJMMnM0ZWpGUWMzUTJURUp5ZG13dmNETnNVR1JxWWtaME1HaHpiRWRMVnpKRlVETTRObkpGVTFaWVNtMWlWMUZ5VkZSR2RETmlkRWRtWkZSWlZsWllSMUIyTlhrNE5WWjRUSGxNTmxGaGIySTFVMEoyWVhScVpHVndhV1FyTlRGbVZYWnJZekkzUWtGU05HSnZPRTlvYkM5WmJWbHJjR28zYlZCV0wwUXZUMVJHUW1oNVRuVkxiVzVLVERSS2QycG1aWFV2YjFacU1uSjNjRlZsWVc4NE9GbEZZbTFQWWxab2RFc3dRVlV2Vm5kNWVrcDVMM1ZFVEd4TGJuQjZjMWxpWVhSdmVscG1kRGxPVnpaaFpsVlhNMlp6TDFWWVRYZHFTWEJZTTNKNFdFZElNbWg0VmtWWmVqUkRiMVJuT0cxVlpucENWVVpSYkdnNVVtaE1NMEkwYlV0cFowNTBMM2hDTTFNMU9WTkhlVVJMUkZaMk5FWmthR1psTVZaUFJYWlJNekp1ZHowOWZRPT1cIi8+PGlucHV0IHR5cGU9XCJoaWRkZW5cIiBuYW1lPVwidHhuVHlwZVwiIGlkPVwidHhuVHlwZVwiIHZhbHVlPVwiNzlcIi8+PGlucHV0IHR5cGU9XCJoaWRkZW5cIiBuYW1lPVwiY2hhbm5lbFR5cGVcIiBpZD1cImNoYW5uZWxUeXBlXCIgdmFsdWU9XCIwN1wiLz48aW5wdXQgdHlwZT1cImhpZGRlblwiIG5hbWU9XCJmcm9udFVybFwiIGlkPVwiZnJvbnRVcmxcIiB2YWx1ZT1cImh0dHA6Ly8xMC4xMC4xMjkuNDA6ODA4Ni9wYXlpbnRlcmZhY2UtZGlzcGF0Y2hlci1mcm9udC9VTklPTlBBWTExMDAwMU5vdGljZS9mcm9udFJjdlJlc3BvbnNlXCIvPjxpbnB1dCB0eXBlPVwiaGlkZGVuXCIgbmFtZT1cImNlcnRJZFwiIGlkPVwiY2VydElkXCIgdmFsdWU9XCI2ODc1OTY2MzEyNVwiLz48aW5wdXQgdHlwZT1cImhpZGRlblwiIG5hbWU9XCJlbmNvZGluZ1wiIGlkPVwiZW5jb2RpbmdcIiB2YWx1ZT1cIlVURi04XCIvPjxpbnB1dCB0eXBlPVwiaGlkZGVuXCIgbmFtZT1cInZlcnNpb25cIiBpZD1cInZlcnNpb25cIiB2YWx1ZT1cIjUuMS4wXCIvPjxpbnB1dCB0eXBlPVwiaGlkZGVuXCIgbmFtZT1cImFjY2Vzc1R5cGVcIiBpZD1cImFjY2Vzc1R5cGVcIiB2YWx1ZT1cIjBcIi8+PGlucHV0IHR5cGU9XCJoaWRkZW5cIiBuYW1lPVwiZW5jcnlwdENlcnRJZFwiIGlkPVwiZW5jcnlwdENlcnRJZFwiIHZhbHVlPVwiNjg3NTk2MjIxODNcIi8+PGlucHV0IHR5cGU9XCJoaWRkZW5cIiBuYW1lPVwicmVxUmVzZXJ2ZWRcIiBpZD1cInJlcVJlc2VydmVkXCIgdmFsdWU9XCJCQ1dETURSQTJQUzRUSzFKQ1ZYR1wiLz48aW5wdXQgdHlwZT1cImhpZGRlblwiIG5hbWU9XCJ0eG5UaW1lXCIgaWQ9XCJ0eG5UaW1lXCIgdmFsdWU9XCIyMDE4MDcwNDIyMzMyN1wiLz48aW5wdXQgdHlwZT1cImhpZGRlblwiIG5hbWU9XCJtZXJJZFwiIGlkPVwibWVySWRcIiB2YWx1ZT1cIjgwMTEwMDA0ODE2MDAwOVwiLz48aW5wdXQgdHlwZT1cImhpZGRlblwiIG5hbWU9XCJhY2NUeXBlXCIgaWQ9XCJhY2NUeXBlXCIgdmFsdWU9XCIwMVwiLz48aW5wdXQgdHlwZT1cImhpZGRlblwiIG5hbWU9XCJzaWduTWV0aG9kXCIgaWQ9XCJzaWduTWV0aG9kXCIgdmFsdWU9XCIwMVwiLz48L2Zvcm0+PC9ib2R5PjxzY3JpcHQgdHlwZT1cInRleHQvamF2YXNjcmlwdFwiPmRvY3VtZW50LmFsbC5wYXlfZm9ybS5zdWJtaXQoKTs8L3NjcmlwdD48L2h0bWw+In0="
		 * ,"sign":"7E3BC47CCB684E93E60FEEB62D3F8CFD","status":"SRP001"} 其中datas解析后的结构为
		 * {"cardId":"1014517287688667137","useableTradeType":null,"html":
		 * "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html;
		 * charset=UTF-8\"/></head><body><form id = \"pay_form\"
		 * action=\"https://gateway.test.95516.com/gateway/api/frontTransReq.do\"
		 * method=\"post\"><input type=\"hidden\" name=\"bizType\" id=\"bizType\"
		 * value=\"000902\"/><input type=\"hidden\" name=\"tokenPayData\"
		 * id=\"tokenPayData\" value=\"{trId=62000000001&tokenType=01}\"/><input
		 * type=\"hidden\" name=\"txnSubType\" id=\"txnSubType\" value=\"00\"/><input
		 * type=\"hidden\" name=\"orderId\" id=\"orderId\"
		 * value=\"BCWDMDRA2PS4TK1JCVXG\"/><input type=\"hidden\" name=\"backUrl\"
		 * id=\"backUrl\"
		 * value=\"http://61.50.130.246:5050/payinterface-dispatcher-front/UNIONPAY110001Notice/openCardNotify\"/><input
		 * type=\"hidden\" name=\"signature\" id=\"signature\"
		 * value=\"YMhc2fAzqq39KDhlqt4R0zAHdzjbXSeLJImbiTEqkvhEIFm5MTrMMwR80qO42kdLXK5fTL7OY5Zj8mWlnfd1RGbA2SPYs5BngH5CIiLXFy4P88sKAb8zMmiuDswpXNp9BVNJcF3LxyGDZyRjRQeODzAckeDMAjI6S81SxrvkX24JIrDU8SuNY9U0zyuy763eWU9dSVbcyqrWN00+hyYRd7m3N/DQwUbqHjpi+D8diN+gAC2YrEAZf4qS4eM8dT0f2MaXB7eP9yifeQmlQwpxxQfU71mP1OUfteE4C1g3i+Pk4ppnUqEGncOL+W2LO8X3T8FAtGNT1qEGcL7wMRqLrA==\"/><input
		 * type=\"hidden\" name=\"accNo\" id=\"accNo\"
		 * value=\"FoIM1ZCPTY1+EKYsgLd+9boOjI0VCnHC89TEpWQmKcHHWKznuwJ9+mctqm49F8+tjtz7V1p2s+wg6+8kksSPl6ljBi4I2TyATfbQlJbwinaL6gPOQjDYK2OZOXwltc0oPmOBeVgV5ePZfAi0lAkhZaqoDmUhStdOVYT41c2iUvV6h1bihIVy84oNJa3aTHxheySGj+UdaFlIgIYqqn/T4bkS/b7U9/hdJiVCIH7chYPS5wroehLmJbLNcS+sUGGGd0cMZ0yAgztCFe1j303Wh+PUox7e4/zThfCk/qMz/b4QCt+ll/7+XIDv30Hy2YdOOiU4mZcvOIbLnZnxI8ZS5w==\"/><input
		 * type=\"hidden\" name=\"customerInfo\" id=\"customerInfo\"
		 * value=\"e2NlcnRpZklkPTM0MTEyNjE5NzcwOTIxODM2NiZjZXJ0aWZUcD0wMSZlbmNyeXB0ZWRJbmZvPVdBTGs3TDYvT3pRUFNsaWJjMUFCWkZNOXBJajhuQjl3VjJIQUp6cHVEU2xGekZqNE9rL2s4ejFQc3Q2TEJydmwvcDNsUGRqYkZ0MGhzbEdLVzJFUDM4NnJFU1ZYSm1iV1FyVFRGdDNidEdmZFRZVlZYR1B2NXk4NVZ4THlMNlFhb2I1U0J2YXRqZGVwaWQrNTFmVXZrYzI3QkFSNGJvOE9obC9ZbVlrcGo3bVBWL0QvT1RGQmh5TnVLbW5KTDRKd2pmZXUvb1ZqMnJ3cFVlYW84OFlFYm1PYlZodEswQVUvVnd5ekp5L3VETGxLbnB6c1liYXRvelpmdDlOVzZhZlVXM2ZzL1VYTXdqSXBYM3J4WEdIMmh4VkVZejRDb1RnOG1VZnpCVUZRbGg5UmhMM0I0bUtpZ050L3hCM1M1OVNHeURLRFZ2NEZkaGZlMVZPRXZRMzJudz09fQ==\"/><input
		 * type=\"hidden\" name=\"txnType\" id=\"txnType\" value=\"79\"/><input
		 * type=\"hidden\" name=\"channelType\" id=\"channelType\" value=\"07\"/><input
		 * type=\"hidden\" name=\"frontUrl\" id=\"frontUrl\"
		 * value=\"http://10.10.129.40:8086/payinterface-dispatcher-front/UNIONPAY110001Notice/frontRcvResponse\"/><input
		 * type=\"hidden\" name=\"certId\" id=\"certId\" value=\"68759663125\"/><input
		 * type=\"hidden\" name=\"encoding\" id=\"encoding\" value=\"UTF-8\"/><input
		 * type=\"hidden\" name=\"version\" id=\"version\" value=\"5.1.0\"/><input
		 * type=\"hidden\" name=\"accessType\" id=\"accessType\" value=\"0\"/><input
		 * type=\"hidden\" name=\"encryptCertId\" id=\"encryptCertId\"
		 * value=\"68759622183\"/><input type=\"hidden\" name=\"reqReserved\"
		 * id=\"reqReserved\" value=\"BCWDMDRA2PS4TK1JCVXG\"/><input type=\"hidden\"
		 * name=\"txnTime\" id=\"txnTime\" value=\"20180704223327\"/><input
		 * type=\"hidden\" name=\"merId\" id=\"merId\" value=\"801100048160009\"/><input
		 * type=\"hidden\" name=\"accType\" id=\"accType\" value=\"01\"/><input
		 * type=\"hidden\" name=\"signMethod\" id=\"signMethod\"
		 * value=\"01\"/></form></body><script
		 * type=\"text/javascript\">document.all.pay_form.submit();</script></html>" }
		 */
		String response = sendRequest(map, bindPayRemitCardUrl);

		Map<String, String> resultMap = JsonUtils.toObject(response, new TypeReference<Map<String, String>>() {
		});
		if (generateSign(resultMap).equals(resultMap.get("sign"))) {
			String datas = new String(Base64.decodeBase64(resultMap.get("datas")), CHARSET);
			Map<String, String> dataMaps = JsonUtils.toObject(datas, new TypeReference<Map<String, String>>() {
			});
			System.out.println(JsonUtils.toJsonString(dataMaps));
		} else {
			logger.error("验签失败...");
		}
	}

	@Test
	public void remotePay() throws Exception {

		Map<String, String> map = new HashMap<>();
		map.put("requestId", UUID.randomUUID().toString());
		/**
		 * 1017315126730891265 1018768881217302530
		 */
		map.put("registId", "1017315126730891265");//1011847073964548098
		map.put("cardId", "1018768881217302530");//1007212212750495746
		map.put("agentNo", "8620805971");
		map.put("transAmount", "0.01");
		map.put("deviceId", "deviceID001");
		map.put("deviceType", "PAD");
		map.put("sourceIp", "127.0.0.1");
		map.put("locationGps", "T38.85325|117.48503");// 经纬度信息( T38.85325|117.48503 )，基站信息（123456123456|123456123456）
		map.put("optionalCustomerNo", "");// 自选商户号
		map.put("system", "business_system");
		map.put("oper", "gms");
		map.put("sign", generateSign(map));

		/**
		 * status=SRP001 请求成功 status=SRP002 请求重复 status=SRPE999处理异常 status=01签名验证失败
		 * status=99系统异常 返回值样例：
		 * {"result":null,"datas":"eyJ0cmFuc1JlcXVlc3RTdGF0dXMiOiJSRVFVRVNUX0lORyIsInRyYW5zSWQiOiIxMDE0NTIxODk3NTEzOTc1ODEwIn0=","sign":
		 * "05479856E4ED45FEA088EE72B41A47DE","status":"SRP001"} 其中datas解析后的结构为
		 * {"transRequestStatus":"REQUEST_ING","transId":"1014521897513975810"}
		 */
		String response = sendRequest(map, payUrl);

		Map<String, String> resultMap = JsonUtils.toObject(response, new TypeReference<Map<String, String>>() {
		});
		if (generateSign(resultMap).equals(resultMap.get("sign"))) {
			System.out.println(JsonUtils.toJsonString(resultMap));
			String datas = new String(Base64.decodeBase64(resultMap.get("datas")), CHARSET);
			Map<String, String> dataMaps = JsonUtils.toObject(datas, new TypeReference<Map<String, String>>() {
			});
			System.out.println(JsonUtils.toJsonString(dataMaps));
		} else {
			logger.error("验签失败...");
		}
	}

	@Test
	public void remoteRemit() throws Exception {
		/**
		 * 1017315126730891265 1018768881217302530
		 */
		Map<String, String> map = new HashMap<>();
		map.put("requestId", UUID.randomUUID().toString());
		map.put("registId", "1011847073964548098");
		map.put("cardId", "1007212212750495746");
		map.put("agentNo", "8620805971");
		map.put("transAmount", "2");
		map.put("system", "business_system");
		map.put("oper", "gms");
		map.put("sign", generateSign(map));
		System.out.println(JsonUtils.toJsonString(map));

		/**
		 * status=SRP001 请求成功 status=SRP002 请求重复 status=SRPE999处理异常 status=01签名验证失败
		 * status=99系统异常 返回值样例：
		 * {"result":null,"datas":"eyJ0cmFuc1JlcXVlc3RTdGF0dXMiOiJSRVFVRVNUX0ZBSUwiLCJ0cmFuc0lkIjoiMTAxNDUyNjQzOTQ5MjE5MDIwOSJ9","sign":
		 * "ADCB3ADF1967C77BEDBF5A9064F89774","status":"SRP001"} 其中datas解析后的结构为
		 * {"transRequestStatus":"REQUEST_FAIL","transId":"1014526439492190209"}
		 */
		String response = sendRequest(map, remtiUrl);

		Map<String, String> resultMap = JsonUtils.toObject(response, new TypeReference<Map<String, String>>() {
		});
		if (generateSign(resultMap).equals(resultMap.get("sign"))) {
			String datas = new String(Base64.decodeBase64(resultMap.get("datas")), CHARSET);
			Map<String, String> dataMaps = JsonUtils.toObject(datas, new TypeReference<Map<String, String>>() {
			});
			System.out.println(JsonUtils.toJsonString(dataMaps));
		} else {
			logger.error("验签失败...");
		}
	}

	@SuppressWarnings({ "resource" })
	private String sendRequest(Map<String, String> target, String url) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = new DefaultHttpClient();
			httpPost = new HttpPost(url);
			// 设置参数
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> iterator = target.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> elem = (Entry<String, String>) iterator.next();
				list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
			}
			if (list.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, CHARSET);
				httpPost.setEntity(entity);
			}
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, CHARSET);
					System.out.println(result);
					return result;
				}
			}
		} catch (Exception ex) {
			logger.error("{}", ex);
		}
		return null;
	}

	private String generateSign(Map<String, String> params) throws Exception {
		ArrayList<String> paramNames = new ArrayList<String>(params.keySet());
		Collections.sort(paramNames);
		// 组织待签名信息
		StringBuilder signSource = new StringBuilder();
		Iterator<String> iterator = paramNames.iterator();
		while (iterator.hasNext()) {
			String paramName = iterator.next();
			if (!"sign".equals(paramName) && !"class".equals(paramName) && Objects.nonNull(params.get(paramName))
					&& (!"".equals(params.get(paramName)))) {
				signSource.append(paramName).append("=").append(params.get(paramName));
				if (iterator.hasNext())
					signSource.append("&");
			}
		}

		String param = signSource.toString();
		if (param.charAt(param.length() - 1) == '&') {
			param = param.substring(0, param.length() - 1);
		}

		return DigestUtils.md5DigestAsHex((param + "&key=" + KEY).getBytes(CHARSET));
	}

}
