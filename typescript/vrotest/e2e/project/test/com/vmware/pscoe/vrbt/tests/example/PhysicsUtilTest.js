describe('Physics utils', () => {
  it('Calculate energy from mass', () => {
    const PhysicsUtil = System.getModule('com.vmware.pscoe.vrbt.tests.example').PhysicsUtil()
    const massInKilos = 15
    const energyInJoules = PhysicsUtil.getObjectEnergy(massInKilos)
    System.log('15 kilograms can be converted to ' + energyInJoules + ' joules')
    expect(energyInJoules).toBe(1348132768105226500)
  })
})
