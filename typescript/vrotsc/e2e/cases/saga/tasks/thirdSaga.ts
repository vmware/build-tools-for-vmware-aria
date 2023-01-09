export default () => print();

function print(): string {
    System.debug("Third saga");
    throw new Error("Error message");
}
