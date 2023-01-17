export default () => print();

function print(): string {
    System.debug("Rolled back Second Saga");
    return "Rolled back Second Saga";
}
