/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.training.indexerpostprocessor;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.RangeTermFilter;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tibor Lipusz
 */
@Component(
	immediate = true,
	property = {"indexer.class.name=com.liferay.portal.kernel.model.User"},
	service = IndexerPostProcessor.class
)
public class UserIndexerPostProcessor implements IndexerPostProcessor {

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter booleanFilter, SearchContext searchContext)
		throws Exception {

		_log.info("postProcessContextBooleanFilter");

		LinkedHashMap<String, Object> params =
			(LinkedHashMap<String, Object>)searchContext.getAttribute("params");

		if (params != null) {
			String lastLoginDate = (String)params.get(_LAST_LOGIN_DATE_FIELD);

			if (Validator.isNotNull(lastLoginDate)) {
				Calendar now = Calendar.getInstance();

				now.set(Calendar.SECOND, 0);

				DateFormat dateFormat =
					DateFormatFactoryUtil.getSimpleDateFormat("yyyyMMddHHmmss");

				String endString = dateFormat.format(now.getTime());

				String startString = normalizeDate(lastLoginDate);

				RangeTermFilter rangeTermFilter = new RangeTermFilter(
					_LAST_LOGIN_DATE_FIELD, true, true, startString, endString);

				booleanFilter.add(rangeTermFilter, BooleanClauseOccur.MUST);
			}
		}
	}

	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

		_log.info("postProcessContextQuery");
	}

	@Override
	public void postProcessDocument(Document document, Object obj)
		throws Exception {

		_log.info("postProcessDocument");

		User user = (User)obj;

		Date lastLoginDate = user.getLastLoginDate();

		if (lastLoginDate != null) {
			document.addDate(_LAST_LOGIN_DATE_FIELD, lastLoginDate);
		}
	}

	@Override
	public void postProcessFullQuery(
			BooleanQuery fullQuery, SearchContext searchContext)
		throws Exception {

		_log.info("postProcessFullQuery");
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter booleanFilter,
			SearchContext searchContext)
		throws Exception {

		_log.info("postProcessSearchQuery");
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		_log.info("postProcessSearchQuery");
	}

	@Override
	public void postProcessSummary(
		Summary summary, Document document, Locale locale, String snippet) {

		_log.info("postProcessSummary");
	}

	private String normalizeDate(String lastLoginDate) {
		Calendar now = Calendar.getInstance();

		now.set(Calendar.SECOND, 0);

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss");

		Calendar pastMinute = (Calendar)now.clone();
		Calendar past30Minutes = (Calendar)now.clone();
		Calendar pastHour = (Calendar)now.clone();
		Calendar pastMonth = (Calendar)now.clone();
		Calendar past24Hours = (Calendar)now.clone();

		if (lastLoginDate.equals("lastMinute")) {
			pastMinute.set(Calendar.MINUTE, now.get(Calendar.MINUTE) - 1);

			return dateFormat.format(pastMinute.getTime());
		}
		else if (lastLoginDate.equals("last30Minutes")) {
			past30Minutes.set(Calendar.MINUTE, now.get(Calendar.MINUTE) - 30);

			return dateFormat.format(past30Minutes.getTime());
		}
		else if (lastLoginDate.equals("lastHour")) {
			pastHour.set(
				Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY) - 1);

			return dateFormat.format(pastHour.getTime());
		}
		else if (lastLoginDate.equals("lastDay")) {
			past24Hours.set(
				Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 1);

			return dateFormat.format(past24Hours.getTime());
		}
		else if (lastLoginDate.equals("lastMonth")) {
			pastMonth.set(Calendar.MONTH, now.get(Calendar.MONTH) - 1);

			return dateFormat.format(pastMonth.getTime());
		}

		return StringPool.BLANK;
	}

	private static final String _LAST_LOGIN_DATE_FIELD = "lastLoginDate";

	private static final Log _log = LogFactoryUtil.getLog(
		UserIndexerPostProcessor.class);

}