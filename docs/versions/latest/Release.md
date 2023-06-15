## Breaking Changes
GitHub issue 
111 typescript vcvirtualpcipassthrough backing doesnt support vcvirtualpcipassthroughvmiopbackinginfo #124

## Issues Description 
When trying to create / attache new PCI device (vGPU), the VcVirtualPCIPassthrough class doesn't support for VcVirtualPCIPassthroughVmiopBackingInfo.vgpu. 

## Improvements
declared a new type cVirtualPCIPassthroughBackingInfo = VcVirtualDeviceBackingInfo & VcVirtualPCIPassthroughDeviceBackingInfo

this way the class "VcVirtualPCIPassthrough" supports vgpu
