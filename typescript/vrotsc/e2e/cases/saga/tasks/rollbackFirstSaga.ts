export default () => print();

function print(): string {
    System.debug("Rolled back First Saga");
    return "Rolled back First Saga";
}
