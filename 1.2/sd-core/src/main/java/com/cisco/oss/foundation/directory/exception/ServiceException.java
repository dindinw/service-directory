/**
 * Copyright 2014 Cisco Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cisco.oss.foundation.directory.exception;


/**
 * The RuntimeException to wrap ServiceDirectory failure and errors.
 *
 * The ServiceException has the ExceptionCode to categorize certain error
 * types.
 *
 *
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -3706093386454084825L;

    private final ErrorCode errorCode;
    private final String errMsgTemplate;
    private final Object[] errMsgArgs;

    @Override
    public String getMessage() {
        return errMsgArgs.length==0 ? errMsgTemplate : String.format(errMsgTemplate, errMsgArgs);
    }

    public ServiceException(ErrorCode errorCode){
        this(errorCode,errorCode.getMessageTemplate());
    }
    public ServiceException(ErrorCode errorCode, String errMsgTemplate, Object ... errMsgArgs){
        this(errorCode, null, errMsgTemplate, errMsgArgs);

    }
    public ServiceException(ErrorCode errorCode,Throwable cause){
        this(errorCode,cause,errorCode.getMessageTemplate());
    }

    public ServiceException(ErrorCode errorCode, Throwable cause, String errMsgTemplate, Object ... errMsgArgs) {
        super(cause);
        this.errorCode =errorCode;
        this.errMsgTemplate = errMsgTemplate;
        this.errMsgArgs = errMsgArgs;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }

    public ServiceDirectoryError getServiceDirectoryError() {
        return new ServiceDirectoryError(errorCode, errMsgArgs);
    }

}
