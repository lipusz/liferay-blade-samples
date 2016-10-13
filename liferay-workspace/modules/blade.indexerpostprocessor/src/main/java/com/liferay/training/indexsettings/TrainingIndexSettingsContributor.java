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

package com.liferay.training.indexsettings;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch.settings.BaseIndexSettingsContributor;
import com.liferay.portal.search.elasticsearch.settings.IndexSettingsContributor;
import com.liferay.portal.search.elasticsearch.settings.TypeMappingsHelper;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tibor Lipusz
 */
@Component(
	immediate = true, service = IndexSettingsContributor.class
)
public class TrainingIndexSettingsContributor
	extends BaseIndexSettingsContributor {

	public TrainingIndexSettingsContributor() {
		super(1);
	}

	public TrainingIndexSettingsContributor(int priority) {
		super(priority);
	}

	@Override
	public void contribute(TypeMappingsHelper typeMappingsHelper) {
		String indexName = _LIFERAY_ES_INDEX_NAME_DEFAULT_PREFIX +
			String.valueOf(CompanyThreadLocal.getCompanyId());

		String trainingTypeMappings = getResourceAsString(
			getClass(), _TRAINING_LIFERAY_ES_TYPE_MAPPING_FILE_NAME);

		_log.info("Contributing to type mapping for index " + indexName);

		try {
			typeMappingsHelper.addTypeMappings(indexName, trainingTypeMappings);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			_log.error(illegalArgumentException);
		}
	}

	/**
	 * See {@link
	 *  com.liferay.portal.search.elasticsearch.internal.util.ResourceUtil.
	 *   getResourceAsString(Class<?>, String)}
	 *
	 * @param clazz
	 * @param resourceName
	 * @return
	 */
	protected String getResourceAsString(Class<?> clazz, String resourceName) {
		try (InputStream inputStream = clazz.getResourceAsStream(
				resourceName)) {

			return StringUtil.read(inputStream);
		}
		catch (IOException ioe) {
			throw new RuntimeException(
				"Unable to load resource: " + resourceName, ioe);
		}
	}

	private static final String _LIFERAY_ES_INDEX_NAME_DEFAULT_PREFIX =
		"liferay-";

	private static final String _TRAINING_LIFERAY_ES_TYPE_MAPPING_FILE_NAME =
		"/META-INF/mappings/training-liferay-type-mappings.json";

	private static final Log _log = LogFactoryUtil.getLog(
		TrainingIndexSettingsContributor.class);

}