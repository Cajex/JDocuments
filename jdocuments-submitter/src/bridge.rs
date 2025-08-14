pub type BridgeRawHandle = *mut std::ffi::c_void;

#[repr(C)]
pub struct BridgeHandle {
    raw: BridgeRawHandle,
}

pub struct BridgeInterface {
    broker_handle: BridgeRawHandle,
    handles: Vec<BridgeHandle>,
}

impl BridgeHandle {
    pub fn raw(&self) -> BridgeRawHandle {
        self.raw
    }
}

impl BridgeInterface {
    //noinspection RsCStringPointer
    pub fn new(manufacturer: String, family: String, product: String) -> Self {
        unsafe {
            let interface = Self {
                broker_handle: bridge_open(
                    std::ffi::CString::new(manufacturer).unwrap().as_ptr(),
                    std::ffi::CString::new(family).unwrap().as_ptr(),
                    std::ffi::CString::new(product).unwrap().as_ptr(),
                ),
                handles: vec![],
            };
            interface
        }
    }

    pub fn open(&mut self) {
        unsafe {
            let ptr: BridgeRawHandle = std::ptr::null_mut();
            let n = bridge_collect(ptr);
            let slice = std::slice::from_raw_parts(ptr, n as usize);
        }
    }
}

impl Drop for BridgeInterface {
    fn drop(&mut self) {
        unsafe {
            bridge_close(self.broker_handle);
        }
    }
}

#[link(name = "bridge")]
unsafe extern "C" {
    pub fn bridge_open(
        manufacturer: *const std::ffi::c_char,
        family: *const std::ffi::c_char,
        product: *const std::ffi::c_char,
    ) -> BridgeRawHandle;

    pub fn bridge_collect(handles: BridgeRawHandle) -> std::ffi::c_uchar;

    pub fn bridge_look_for(product: *const std::ffi::c_char) -> BridgeRawHandle;

    pub fn bridge_get_information(handle: BridgeRawHandle) -> BridgeHandle;

    fn bridge_close(handle: BridgeRawHandle) -> std::ffi::c_uchar;
}
