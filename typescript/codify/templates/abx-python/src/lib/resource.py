import json


class Resource:

    def __init__(self, context, input):
        self.context = context
        self.input = input

    def create(self):
        ''' Create a new resource '''

        return {
            'status': f"Resource '{self.input.get('name')}' created"
        }

    def remove(self):
        ''' Remove existing resource '''

        return {
            'status': f"Resource '{self.input.get('name')}' removed"
        }

    def make_internal_request(self):
        ''' Create an internal GET reqest and return the response. '''

        data = self.context.request('/deployment/api/deployments', 'GET', None)
        return json.loads(data['content'])

    def make_external_request(self):
        ''' Create an external POST reqest and return the response. '''

        payload = {
            'userId': 1,
            'title': 'My TODO item',
            'completed': False
        }
        data = self.context.request(
            'https://jsonplaceholder.typicode.com/todos', 'POST', payload)
        return json.loads(data['content'])
