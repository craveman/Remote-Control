# Sm02Client

A description of this package.

1. handle errors;
2. skip unknown commands (tags);
3. 0x24 (auth resp) - take a look at this:
```
<RemoteControl.PointsStepperViewController: 0x105e28300>
<RemoteControl.PointsStepperViewController: 0x105e28300>
<RemoteControl.PointsStepperViewController: 0x105dd58e0>
<RemoteControl.PointsStepperViewController: 0x105dd58e0>
<RemoteControl.PointsViewController: 0x105dd3330>
<RemoteControl.TimersTableViewController: 0x105dd69d0>
ERROR: The 'authentication' message doesn't have 'status' field
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message \'36\' doesn\'t have a decoder")
WARN: there is no messages processor for {} client [IPv4]192.168.88.254/192.168.88.254:21074
ERROR: The 'broadcast' message doesn't have 'weapon' field
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message \'11\' doesn\'t have a decoder")
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message doen\'t have a decoder for the tag - \'102\'")
WARN: there is no messages processor for {} client [IPv4]192.168.88.254/192.168.88.254:21074
ERROR: The 'broadcast' message doesn't have 'weapon' field
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message \'11\' doesn\'t have a decoder")
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message doen\'t have a decoder for the tag - \'102\'")
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message doen\'t have a decoder for the tag - \'102\'")
ERROR: The 'broadcast' message doesn't have 'weapon' field
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message \'11\' doesn\'t have a decoder")
ERROR: The 'broadcast' message doesn't have 'weapon' field
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message \'11\' doesn\'t have a decoder")
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message doen\'t have a decoder for the tag - \'102\'")
ERROR: The 'broadcast' message doesn't have 'weapon' field
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message \'11\' doesn\'t have a decoder")
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message doen\'t have a decoder for the tag - \'102\'")
ERROR: The 'broadcast' message doesn't have 'weapon' field
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message \'11\' doesn\'t have a decoder")
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message doen\'t have a decoder for the tag - \'102\'")
ERROR: The 'broadcast' message doesn't have 'weapon' field
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message \'11\' doesn\'t have a decoder")
ERROR: during processing request from '{}', this error occured - {} [IPv4]192.168.88.254/192.168.88.254:21074 parsingdError("The message doen\'t have a decoder for the tag - \'102\'")
```
