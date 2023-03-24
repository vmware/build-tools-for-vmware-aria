describe('echo', function () {
  it('should concat inputs ', function () {
    const input = 'hello'
    const output = System.getModule('org.example').echo(input)
    expect(output).toEqual(input)
  })
  xit('should do nothing as it is x-ed', function () {
    expect(true).toBe(false)
  })
})
