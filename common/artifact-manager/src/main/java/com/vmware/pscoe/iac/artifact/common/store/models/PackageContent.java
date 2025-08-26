/*
 * #%L
 * artifact-manager
 * %%
 * Copyright (C) 2023 VMware
 * %%
 * Build Tools for VMware Aria
 * Copyright 2023 VMware, Inc.
 * 
 * This product is licensed to you under the BSD-2 license (the "License"). You may not use this product except in compliance with the BSD-2 License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices and license terms. Your use of these subcomponents is subject to the terms and conditions of the subcomponent's license, as noted in the LICENSE file.
 * #L%
 */
package com.vmware.pscoe.iac.artifact.common.store.models;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class PackageContent<T extends PackageContent.ContentType> {
	private static final int PRIME_NUMBER_17 = 17;
	private static final int PRIME_NUMBER_31 = 31;

	public interface ContentType {
	}

	public static class Content<T> {

		private final T type;
		private final String id;
		private final String name;

		public Content(T type, String id, String name) {
			this.type = type;
			this.id = id;
			this.name = name;
		}

		public T getType() {
			return type;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if (obj == null || !this.getClass().equals(obj.getClass())) {
				return false;
			}
			Content a = this;
			Content b = (Content) obj;
			return a.type.equals(b.type) && a.id.equalsIgnoreCase(b.id) && a.name.equalsIgnoreCase(b.name);
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(PRIME_NUMBER_17, PRIME_NUMBER_31)
					.append(id)
					.append(name)
					.append(type)
					.toHashCode();
		}

		@Override
		public String toString() {
			return String.format("Type: %s Id: %s Name: %s", this.type, this.id, this.name);
		}
	}

	private final List<Content<T>> content;

	protected PackageContent(List<Content<T>> content) {
		this.content = Collections.unmodifiableList(content);
	}

	public List<Content<T>> getContent() {
		return content;
	}
}
