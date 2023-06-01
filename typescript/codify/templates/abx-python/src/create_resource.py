import sys
from os.path import dirname
sys.path.append(dirname(__file__))
from lib.resource import Resource


def handler(context, input):
    resource = Resource(context, input)
    return resource.create()
