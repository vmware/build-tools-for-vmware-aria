import unittest
import json
from unittest.mock import MagicMock, Mock
from lib.resource import Resource


class TestResource(unittest.TestCase):

    def test_create_resource(self):
        # Run code under test
        resource = Resource(None, {'name': 'Test resource'})
        result = resource.create()

        # Assert results
        self.assertEqual(type(result), dict)
        self.assertTrue('status' in result)
        self.assertEqual(type(result['status']), str)

    def test_remove_resource(self):
        # Run code under test
        resource = Resource(None, {'name': 'Test resource'})
        result = resource.remove()

        # Assert results
        self.assertEqual(type(result), dict)
        self.assertTrue('status' in result)
        self.assertEqual(type(result['status']), str)

    def test_internal_request(self):
        # Build mocks
        context = MagicMock()
        context.request = Mock(
            return_value={'content': json.dumps({'name': 'Test'})})

        # Run code under test
        resource = Resource(context, {})
        result = resource.make_internal_request()

        # Assert results
        self.assertEqual(type(result), dict)
        self.assertTrue('name' in result)
        self.assertEqual(type(result['name']), str)

    def test_external_request(self):
        # Build mocks
        context = MagicMock()
        context.request = Mock(
            return_value={'content': json.dumps({'name': 'Test'})})

        # Run code under test
        resource = Resource(context, {})
        result = resource.make_external_request()

        # Assert results
        self.assertEqual(type(result), dict)
        self.assertTrue('name' in result)
        self.assertEqual(type(result['name']), str)
