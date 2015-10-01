import sys, glob
sys.path.append('../../../src-gen/main/gen-py')

from score import ScoringService
from score.ttypes import *

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol


socket = TSocket.TSocket('localhost', 9090)
transport = TTransport.TBufferedTransport(socket)
protocol = TBinaryProtocol.TBinaryProtocol(transport)
client = ScoringService.Client(protocol)
transport.open()
print("\n=== List of models provided by server ===")
print(" => client.listModels()")
print(" <= {}\n".format(client.listModels()))

print("\n=== Deploy pojo from file ===")
f = open("../resources/pojos/gbm_02c92461_25ea_45d2_9bf9_af2fa92758d2.jar", "rb")
binarycontent=f.read()
print(" => model_pojo = client.deployPojoJar(binarycontent)")
model_pojo = client.deployPojoJar(binarycontent)
print(" <= {}\n".format(model_pojo))

row = { "Sepal.Length" : 6.4,
        "Sepal.Width"  : 3.1,
        "Petal.Length" : 5.5,
        "Petal.Width"  : 1.8,
        }
print("\n=== Make prediction on row ===")
print(" => client.predictMapRow({}, {})".format(model_pojo.name, row))
prediction = client.predictMapRow(model_pojo.name, row)
print(" <= {}".format(prediction))

transport.close()
