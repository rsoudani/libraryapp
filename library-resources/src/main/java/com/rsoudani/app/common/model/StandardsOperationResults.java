package com.rsoudani.app.common.model;

import com.rsoudani.app.common.exception.FieldNotValidException;

public final class StandardsOperationResults {

	private StandardsOperationResults() {
	}

	public static OperationResult getOperationResultExistent(final ResourceMessage resourceMessage,
			final String fieldsNames) {
		return OperationResult.error(resourceMessage.getKeyOfResourceExistent(),
				resourceMessage.getMessageOfResourceExistent(fieldsNames));
	}

	public static OperationResult getOperationResultInvalidField(final ResourceMessage resourceMessage,
			final FieldNotValidException ex) {
		return OperationResult.error(resourceMessage.getKeyOfInvalidField(ex.getFieldName()), ex.getMessage());
	}

	public static OperationResult getOperationResultNotFound(final ResourceMessage resourceMessage) {
		return OperationResult.error(resourceMessage.getKeyOfResourceNotFound(),
				resourceMessage.getMessageOfResourceNotFound());
	}

}