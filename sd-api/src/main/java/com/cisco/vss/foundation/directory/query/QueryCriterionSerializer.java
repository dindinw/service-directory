/**
 * Copyright (c) 2013-2014 by Cisco Systems, Inc. 
 * All rights reserved. 
 */
package com.cisco.vss.foundation.directory.query;

import java.util.ArrayList;
import java.util.List;

import com.cisco.vss.foundation.directory.query.ServiceInstanceQuery.ContainQueryCriterion;
import com.cisco.vss.foundation.directory.query.ServiceInstanceQuery.EqualQueryCriterion;
import com.cisco.vss.foundation.directory.query.ServiceInstanceQuery.InQueryCriterion;
import com.cisco.vss.foundation.directory.query.ServiceInstanceQuery.NotContainQueryCriterion;
import com.cisco.vss.foundation.directory.query.ServiceInstanceQuery.NotEqualQueryCriterion;
import com.cisco.vss.foundation.directory.query.ServiceInstanceQuery.NotInQueryCriterion;
import com.cisco.vss.foundation.directory.query.ServiceInstanceQuery.PatternQueryCriterion;

/**
 * It is a serializer tool to serialze/deserialize QueryCriterion.
 * 
 * @author zuxiang
 *
 */
public class QueryCriterionSerializer {

	/**
	 * serialize the QueryCriterion to a String expression.
	 * 
	 * This String expression can be deserialized by the deserialze method back.
	 * 
	 * @param queryCriterion
	 * 		the QueryCriterion.
	 * @return
	 * 		the String expression.
	 */
	public static String serialize(QueryCriterion queryCriterion){
		if(queryCriterion instanceof EqualQueryCriterion){
			EqualQueryCriterion criterion = (EqualQueryCriterion) queryCriterion;
			return criterion.getMetadataKey() + " equals " + escapeString(criterion.getCriterion());
		} else if(queryCriterion instanceof NotEqualQueryCriterion){
			NotEqualQueryCriterion criterion = (NotEqualQueryCriterion) queryCriterion;
			return criterion.getMetadataKey() + " not equals " + escapeString(criterion.getCriterion());
		} else if(queryCriterion instanceof ContainQueryCriterion){
			ContainQueryCriterion criterion = (ContainQueryCriterion) queryCriterion;
			return criterion.getMetadataKey();
		} else if(queryCriterion instanceof NotContainQueryCriterion){
			NotContainQueryCriterion criterion = (NotContainQueryCriterion) queryCriterion;
			return "not " + criterion.getMetadataKey();
		} else if(queryCriterion instanceof InQueryCriterion){
			InQueryCriterion criterion = (InQueryCriterion) queryCriterion;
			StringBuilder sb = new StringBuilder(criterion.getMetadataKey());
			sb.append(" in [ ");
			for(String s : criterion.getCriterion()){
				sb.append(escapeString(s)).append(", ");
			}
			sb.append("]");
			return sb.toString();
		} else if(queryCriterion instanceof NotInQueryCriterion){
			NotInQueryCriterion criterion = (NotInQueryCriterion) queryCriterion;
			StringBuilder sb = new StringBuilder(criterion.getMetadataKey());
			sb.append(" not in [ ");
			for(String s : criterion.getCriterion()){
				sb.append(escapeString(s)).append(", ");
			}
			sb.append("]");
			return sb.toString();
		} else if(queryCriterion instanceof PatternQueryCriterion){
			PatternQueryCriterion criterion = (PatternQueryCriterion) queryCriterion;
			return criterion.getMetadataKey() + " matches " + escapeString(criterion.getCriterion());
		}
		return "";
	}
	
	/**
	 * Deserialize a QueryCriterion statement String expression to QueryCriterion.
	 * 
	 * @param statement
	 * 		the statement String expression.
	 * @return
	 * 		the QueryCriterion.
	 */
	public static QueryCriterion deserialize(String statement){
		char [] delimeter = {' ', '\t', ',', '[', ']'}; 
		List<String> cmdList = splitStringByDelimeters(statement, delimeter, true);
		List<String> tokenList = filterDelimeterElement(cmdList, delimeter);
		if(tokenList.size() == 1){
			String key = cmdList.get(0);
			if(! key.isEmpty()){
				return new ContainQueryCriterion(key);
			}
		} else if(tokenList.size() == 2){
			String cmd = cmdList.get(0);
			String key = cmdList.get(1);
			if(! key.isEmpty() && cmd.equals("not")){
				return new NotContainQueryCriterion(key);
			}
		} else {
			String key = tokenList.get(0);
			String op = tokenList.get(1);
			if(op.equals("not")){
				op = op + " " + tokenList.get(2);
			}
			
			if(op.equals("equals")){
				String value = unescapeString(tokenList.get(2));
				if(value != null){
					return new EqualQueryCriterion(key, value);	
				}
			} else if(op.equals("not equals")){
				String value = unescapeString(tokenList.get(3));
				if(value != null){
					return new NotEqualQueryCriterion(key, value);
				}
			} else if(op.equals("matches")){
				String value = unescapeString(tokenList.get(2));
				if(value != null){
					return new PatternQueryCriterion(key, value);
				}
			} else if(op.equals("in")){
				List<String> values = new ArrayList<String>();
				boolean start = false;
				for(String s : cmdList){
					if(s.isEmpty() || s.equals(",")){
						continue;
					} else if(s.equals("[")){
						start = true;
						continue;
					} else if(s.equals("]")){
						start = false;
						break;
					} else if(start){
						String v = unescapeString(s);
						if(v != null){
							values.add(v);
						}
					}
				}
				return new InQueryCriterion(key, values);
			} else if(op.equals("not in")){
				List<String> values = new ArrayList<String>();
				boolean start = false;
				for(String s : cmdList){
					if(s.isEmpty() || s.equals(',')){
						break;
					} else if(s.equals('[')){
						start = true;
						continue;
					} else if(s.equals(']')){
						start = false;
						break;
					} else if(start){
						String v = unescapeString(s);
						if(v != null){
							values.add(v);
						}
						
					}
				}
				return new NotInQueryCriterion(key, values);
			}
		}
		return null;
	}
	
	/**
	 * Deserialize the ServiceInstanceQuery command line String expression.
	 * 
	 * Deserialize the ServiceInstanceQuery command line to the ServiceInstanceQuery.
	 * 
	 * @param cli
	 * 		the ServiceInstanceQuery command line String expression.
	 * @return
	 * 		the QueryCriterion statement String list.
	 */
	public static ServiceInstanceQuery deserializeServiceInstanceQuery(String cli){
		char [] delimeter = {';'}; 
		ServiceInstanceQuery query = new ServiceInstanceQuery();
		for(String statement : splitStringByDelimeters(cli, delimeter, false)){
			QueryCriterion criterion = deserialize(statement);
			if(criterion != null){
				query.addQueryCriterion(criterion);
			}
		};
		return query;
	}
	
	/**
	 * Serialze the ServiceInstanceQuery to the command line string expression.
	 * 
	 * @param query
	 * 		the ServiceInstanceQuery.
	 * @return
	 * 		the string expression.
	 */
	public static String serializeServiceInstanceQuery(ServiceInstanceQuery query){
		List<QueryCriterion> criteria = query.getCriteria();
		StringBuilder sb = new StringBuilder();
		for(QueryCriterion criterion : criteria){
			String statement = serialize(criterion);
			if(statement != null && ! statement.isEmpty()){
				sb.append(statement).append(";");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Escape the String for the QueryCriterion String.
	 * 
	 * Add " to the head and tail, switch " to \".
	 * 
	 * @param str
	 * 		the origin String.
	 * @return
	 * 		the escaped String
	 */
	private static String escapeString(String str){
		return "\"" +  str.replaceAll("\"", "\\\\\"") + "\"";
	}
	
	/**
	 * Unescape the QueryCriterion String.
	 * 
	 * remove the head and tail ", switch \" to ".
	 * 
	 * @param str
	 * 		the escaped String.
	 * @return
	 * 		the origin String.
	 */
	private static String unescapeString(String str){
		if(str.startsWith("\"") && str.endsWith("\"")){
			String s = str.replaceAll("\\\\\"", "\"");
			return s.substring(1, s.length() -1);
		}
		return null;
	}
	
	/**
	 * Filter the delimeter from the String array.
	 * 
	 * remove the String element in the delimeter.
	 * 
	 * @param arr
	 * 		the target array.
	 * @param delimeter
	 * 		the delimeter array need to remove.
	 * @return
	 * 		the array List.
	 */
	private static List<String> filterDelimeterElement(List<String> arr, char[] delimeter){
		List<String> list = new ArrayList<String>();
		for(String s : arr){
			if(s == null || s.isEmpty()){
				continue;
			}
			if(s.length() > 1){
				list.add(s);
				continue;
			}
			char strChar = s.charAt(0);
			boolean find = false;
			for(char c : delimeter){
				if(c == strChar){
					find = true;
					break;
				}
			}
			if(find == false){
				list.add(s);
			}
		}
		return list;
	}
	
	/**
	 * Split a complete String to String array by the delimeter array.
	 * 
	 * This method used to split a command or a statement to sub elements.
	 * It will trim the sub elements too.
	 * 
	 * @param str
	 * 		the complete string.
	 * @param delimeter
	 * 		the delimeter array.
	 * @param includeDeli
	 * 		if true, include the delimeter in the return array.
	 * @return
	 * 		the splited String array.
	 */
	private static List<String> splitStringByDelimeters(String str, char[] delimeter, boolean includeDeli){
		List<String> arr = new ArrayList<String>();
		int i = 0, start = 0, quota = 0;
		char pre = 0;
		for(char c : str.toCharArray()){
			if(c == '"' && pre != '\\'){
				quota ++;
			}
			if(quota % 2 == 0){
				for(char deli : delimeter){
					if( c == deli){
						if(includeDeli){
							arr.add(str.substring(start, i).trim());
							start = i;
						}else if(i > start ){
							arr.add(str.substring(start, i).trim());
							start = i + 1;
						}
						
					}
				}

			} 
			i ++;
			pre = c;
		}
		
		if(includeDeli){
			arr.add(str.substring(start, i).trim());
			start = i;
		}else if(i > start ){
			arr.add(str.substring(start, i).trim());
		}
		
		return arr;
	}
	
//	public static void main(String[] args){
//	String a = "a equals \"c\\\",[] c\"; solution in [ \"s\\\"1\", \"s2\"]; datacenter; not local;";
//	System.out.println(escapeString("ddad\"dd"));
//	String s = "\"ddad\\\"dd\"";
//	System.out.println("---->" + s);
//	System.out.println(unescapeString(s));
//	
//	List<String> stats = deserializeCommandLine(a);
//	for(String stat : stats){
//		QueryCriterion c = deserialize(stat);
//		System.out.println("--------------------");
//		System.out.println(c.toString());
//		System.out.println(serialize(c));
//	}
//}
}
