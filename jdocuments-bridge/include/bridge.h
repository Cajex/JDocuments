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
  TW_UINT32   id;
  TW_VERSION  version;
  TW_UINT16   protocol_maj;
  TW_UINT16   protocol_min;
  TW_UINT32   supported_grps;
  TW_STR32    manufacturer;
  TW_STR32    product_famiy;
  TW_STR32    product_name;
} BridgeInformationRequest;

BRIDGE_PUBLIC typedef struct {
  TW_UINT16 major_num;
  TW_UINT16 minor_num;
  TW_UINT16 language;
  TW_UINT16 country;
  TW_STR32 info;
} BridgeInformationRequestVersion;

/**
* queries the next device.
* @return Callback result whether a driver was found for the device.
*/
BRIDGE_PUBLIC unsigned char bridge_select_next(BridgeRawHandle*);
/**
 * opens the interface between device broker and application.
 * @param manufacturer of application
 * @param family of application
 * @param product name of application
 * @return handle of the broker.
 */
BRIDGE_PUBLIC BridgeRawHandle bridge_open(const char* manufacturer, const char* family, const char* product);

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
BRIDGE_PUBLIC BridgeInformationRequest bridge_get_invortmation(BridgeRawHandle);

/**
 * closes the interface between device broker and application.
 * @return Callback result whether the broker query was successful.
 */
BRIDGE_PUBLIC unsigned char bridge_close(BridgeRawHandle);
