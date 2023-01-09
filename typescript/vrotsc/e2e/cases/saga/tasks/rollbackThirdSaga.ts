export default () => print();

function print(): string {
    System.debug("Rolled back Third Saga");
    return "Rolled back Third Saga";
}
