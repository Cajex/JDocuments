#include "include/bridge.h"

int bridge_select_next(BridgeHandle*) {
    return 0;
}

BridgeHandle bridge_open(const char *manufacturer, const char *family, const char *product) {
    return NULL;
}

size_t bridge_open_handles(BridgeHandle*handles) {
    return 0;
}

BridgeHandle bridge_look_for(const char *product) {
    return NULL;
}

BridgeDriverInvortmation bridge_get_invortmation(BridgeHandle) {
    return (BridgeDriverInvortmation){};
}

int bridge_close(BridgeHandle) {
    return 0;
}
