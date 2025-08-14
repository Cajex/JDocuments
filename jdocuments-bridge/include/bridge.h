#pragma once
#if defined _WIN32 || defined __CYGWIN__
  #ifdef BUILDING_BRIDGE
    #define BRIDGE_PUBLIC __declspec(dllexport)
  #else
    #define BRIDGE_PUBLIC __declspec(dllimport)
  #endif
#else
  #ifdef BUILDING_BRIDGE
      #define BRIDGE_PUBLIC __attribute__ ((visibility ("default")))
  #else
      #define BRIDGE_PUBLIC
  #endif
#endif
#include "twain.h"

BRIDGE_PUBLIC typedef void* BridgeHandle;

BRIDGE_PUBLIC typedef struct {

} BridgeDriverInvortmation;

extern "C" {
/**
 * queries the next device.
 * @return Callback result whether a driver was found for the device.
 */
int bridge_select_next(BridgeHandle*);

/**
 * opens the interface between device broker and application.
 * @param manufacturer of application
 * @param family of application
 * @param product name of application
 * @return handle of the broker.
 */
BRIDGE_PUBLIC BridgeHandle bridge_open(const char* manufacturer, const char* family, const char* product);

/**
 * queries all available handles.
 * @param handles array pointer to queried handle.
 * @return count of available handles.
 */
BRIDGE_PUBLIC size_t bridge_open_handles(BridgeHandle* handles);

/**
 * queries devices for their name.
 * @param product device name
 * @return queried handle
 */
BRIDGE_PUBLIC BridgeHandle bridge_look_for(const char* product);

/**
 *
 * @return provided information of a handle.
 */
BRIDGE_PUBLIC BridgeDriverInvortmation bridge_get_invortmation(BridgeHandle);

/**
 * closes the interface between device broker and application.
 * @return Callback result whether the broker query was successful.
 */
BRIDGE_PUBLIC int bridge_close(BridgeHandle);
}