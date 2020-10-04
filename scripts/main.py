import sys
import requests
from requests_toolbelt.multipart.encoder import MultipartEncoder

dirs = sys.argv[1:]
result = []
url = 'http://moral-helper.online:23456/upload'
flag = False

for i in dirs:
    part = MultipartEncoder(fields={
        'userId': '1',
        'file': ('1.png', open(i, 'rb'), 'application/octet-stream')
    })
    head = {'token': 'c1c5719e761241c3ab3e1627286b9647',
            'content-type': part.content_type}
    response = requests.post(url, data=part, headers=head)
    if response.status_code != 200:
        flag = True
        break
    else:
        result.append(response.text)

if flag:
    print('Fail')
else:
    print('Upload Success:')
    for i in result:
        print(i)
