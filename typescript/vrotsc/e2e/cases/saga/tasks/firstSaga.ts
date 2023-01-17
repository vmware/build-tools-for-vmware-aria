export default () => print();

function print(): string {
    System.debug("First Saga");
    return "Printed this first message";
}
