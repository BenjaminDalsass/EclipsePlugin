package org.deltadore.planet.swt;

import org.deltadore.planet.tools.C_ToolsSWT;
import org.eclipse.swt.graphics.Image;

public enum E_NotificationType {
    ERROR(C_ToolsSWT.f_GET_IMAGE("error.png")),
    DELETE(C_ToolsSWT.f_GET_IMAGE("delete.png")),
    WARN(C_ToolsSWT.f_GET_IMAGE("warn.png")),
    SUCCESS(C_ToolsSWT.f_GET_IMAGE("ok.png")),
    INFO(C_ToolsSWT.f_GET_IMAGE("info.png")),
    LIBRARY(C_ToolsSWT.f_GET_IMAGE("library.png")),
    HINT(C_ToolsSWT.f_GET_IMAGE("hint.png")),
    PRINTED(C_ToolsSWT.f_GET_IMAGE("printer.png")),
    CONNECTION_TERMINATED(C_ToolsSWT.f_GET_IMAGE("terminated.png")),
    CONNECTION_FAILED(C_ToolsSWT.f_GET_IMAGE("connecting.png")),
    CONNECTED(C_ToolsSWT.f_GET_IMAGE("connected.png")),
    DISCONNECTED(C_ToolsSWT.f_GET_IMAGE("disconnected.png")),
    TRANSACTION_OK(C_ToolsSWT.f_GET_IMAGE("ok.png")),
    TRANSACTION_FAIL(C_ToolsSWT.f_GET_IMAGE("error.png"));

    private Image _image;

    private E_NotificationType(Image img) {
        _image = img;
    }

    public Image getImage() {
        return _image;
    }
}
