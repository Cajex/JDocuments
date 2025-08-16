use std::ffi::CString;

pub type BridgeInterfaceHandle<'a> = BridgeHandle<'a>;

#[derive(Clone)]
pub struct BridgeHandle<'a>(&'a BridgeRawHandle);

pub type BridgeRawHandle = *mut std::ffi::c_void;

impl<'a> BridgeHandle<'a> {
    pub fn raw(&self) -> &'a BridgeRawHandle {
        self.0
    }

    pub fn dummy() -> *mut std::ffi::c_double {
        0 as *mut std::ffi::c_double
    }
}

impl From<BridgeRawHandle> for BridgeHandle<'_> {
    fn from(value: BridgeRawHandle) -> Self {
        unsafe {
            let handle: *const BridgeRawHandle = value as *const BridgeRawHandle;
            Self { 0: &*handle }
        }
    }
}

impl Drop for BridgeHandle<'_> {
    fn drop(&mut self) {
        unsafe {
            bridge_drop_handle(*self.raw());
        }
    }
}

#[repr(C)]
#[derive(Debug, Copy, Clone)]
pub struct BridgeInformationRequest {
    pub id: u32,
    pub version: BridgeInformationRequestVersion,
    pub protocol_major: u16,
    pub protocol_minor: u16,
    pub supported_groups: u32,
    pub manufacturer: [u8; 34],
    pub product_family: [u8; 34],
    pub product_name: [u8; 34],
}

#[repr(C)]
#[derive(Debug, Copy, Clone)]
pub struct BridgeInformationRequestVersion {
    pub major_num: u16,
    pub minor_num: u16,
    pub language: u16,
    pub country: u16,
    pub info: [u8; 34], // TW_STR32 = 34 chars
}

#[link(name = "bridge")]
unsafe extern "C" {
    fn bridge_open(
        manufacturer: *const std::ffi::c_char,
        family: *const std::ffi::c_char,
        product: *const std::ffi::c_char,
    ) -> BridgeRawHandle;

    fn bridge_slect_next(handle: BridgeRawHandle) -> std::ffi::c_uchar;

    fn bridge_look_for(product: *const std::ffi::c_char) -> BridgeRawHandle;

    fn bridge_get_information(handle: BridgeRawHandle) -> BridgeInformationRequest;

    fn bridge_close(handle: BridgeRawHandle) -> std::ffi::c_uchar;

    fn bridge_drop_handle(handle: BridgeRawHandle);
}

pub fn open_bridge<'a>(manufacturer: &str, family: &str, product: &str) -> BridgeHandle<'a> {
    unsafe {
        let raw = bridge_open(
            CString::new(manufacturer).unwrap().into_raw(),
            CString::new(family).unwrap().into_raw(),
            CString::new(product).unwrap().into_raw(),
        );
        BridgeHandle::from(raw)
    }
}

pub fn select_next<'a>() -> Option<BridgeHandle<'a>> {
    unsafe {
        let ptr = BridgeHandle::dummy();
        let r = bridge_slect_next(ptr as BridgeRawHandle);
        if r == 0 {
            Some(BridgeHandle::from(ptr as BridgeRawHandle))
        } else {
            None
        }
    }
}

pub fn collect<'a>() -> Vec<BridgeHandle<'a>> {
    let mut handles = Vec::new();
    loop {
        let next = select_next();
        match next {
            None => {
                break;
            }
            Some(handle) => {
                handles.push(handle);
            }
        }
    }
    handles
}

pub fn request_information(handle: BridgeHandle<'_>) -> BridgeInformationRequest {
    unsafe { bridge_get_information(*handle.raw()) }
}

pub fn close_bridge(handle: BridgeInterfaceHandle<'_>) {
    unsafe {
        bridge_close(*handle.raw());
    }
}
