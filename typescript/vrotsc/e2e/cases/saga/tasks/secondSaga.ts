export default () => print();

function print(): string {
    System.debug("Second saga");
    return "Printed this second message";
}
