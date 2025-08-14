#include "include/bridge.h"

int bridge_select_next(BridgeRawHandle*) {
    return 0;
}

BridgeRawHandle bridge_open(const char *manufacturer, const char *family, const char *product) {
    return NULL;
}

unsigned char bridge_collect(BridgeRawHandle*handles) {
    return 0;
}

BridgeRawHandle bridge_look_for(const char *product) {
    return NULL;
}

BridgeHandle bridge_get_invortmation(BridgeRawHandle) {
    return (BridgeHandle){};
}

unsigned char bridge_close(BridgeRawHandle) {
    return 0;
}
