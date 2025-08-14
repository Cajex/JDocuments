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

BRIDGE_PUBLIC typedef void* BridgeRawHandle;

BRIDGE_PUBLIC typedef struct {
  BridgeRawHandle handle;
} BridgeHandle;

/**
 * queries the next device.
 * @return Callback result whether a driver was found for the device.
 */
int bridge_select_next(BridgeRawHandle*);

extern "C" {
/**
 * opens the interface between device broker and application.
 * @param manufacturer of application
 * @param family of application
 * @param product name of application
 * @return handle of the broker.
 */
BRIDGE_PUBLIC BridgeRawHandle bridge_open(const char* manufacturer, const char* family, const char* product);

/**
 * queries all available handles.
 * @param handles array pointer to queried handle.
 * @return count of available handles.
 */
BRIDGE_PUBLIC unsigned char bridge_collect(BridgeRawHandle* handles);

/**
 * queries devices for their name.
 * @param product device name
 * @return queried handle
 */
BRIDGE_PUBLIC BridgeRawHandle bridge_look_for(const char* product);

/**
 *
 * @return provided information of a handle.
 */
BRIDGE_PUBLIC BridgeHandle bridge_get_invortmation(BridgeRawHandle);

/**
 * closes the interface between device broker and application.
 * @return Callback result whether the broker query was successful.
 */
BRIDGE_PUBLIC unsigned char bridge_close(BridgeRawHandle);
}