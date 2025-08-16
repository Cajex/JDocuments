use crate::bridge::BridgeInterfaceHandle;

pub struct ScannerPool<'a> {
    i: BridgeInterfaceHandle<'a>,
    handles: Vec<BridgeInterfaceHandle<'a>>,
}

impl<'a> ScannerPool<'a> {

}