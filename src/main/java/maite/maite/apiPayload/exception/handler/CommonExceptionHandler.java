package maite.maite.apiPayload.exception.handler;


import maite.maite.apiPayload.code.BaseErrorCode;
import maite.maite.apiPayload.exception.GeneralException;


public class CommonExceptionHandler extends GeneralException {

    public CommonExceptionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }

}