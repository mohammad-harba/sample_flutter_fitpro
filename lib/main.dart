import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_blue/flutter_blue.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = const MethodChannel('samples.flutter.dev/fitpro');

  var devices = [];

  void startMeasure() {
    platform.invokeMethod("startMeasure");
  }

  void connectDevice(String name, String mac, String id) {
    platform.invokeMethod(
      'connectDevice',
      <String, dynamic>{"name": name, "mac": mac, "id": id},
    ).catchError((e) => print('Exception caught by WUtil: $e'));
  }

  void scanBluetooth() {
    FlutterBlue flutterBlue = FlutterBlue.instance;
    // Start scanning
    flutterBlue.startScan(timeout: Duration(seconds: 4));

// Listen to scan results
    flutterBlue.scanResults.listen((results) {
      // do something with scan results

      //devices = results;
      for (ScanResult r in results) {
        if (r.device.name.contains("LH716")) {
          if (!devices.contains(r)) {
            devices.add(r);
          }

          setState(() {
            print('setState is called and devices is --------' +
                devices.toString());
          });
        }
      }
    });

// Stop scanning
    flutterBlue.stopScan();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          children: [
            SizedBox(
              height: 50,
            ),
            RaisedButton(
              child: Text(
                "scan for devices",
                style: TextStyle(fontSize: 22),
              ),
              onPressed: () {
                scanBluetooth();
              },
            ),
            SizedBox(
              height: 30,
            ),
            RaisedButton(
              child: Text(
                "start measure",
                style: TextStyle(fontSize: 22),
              ),
              onPressed: () {
                startMeasure();
              },
            ),
            SizedBox(
              height: 100,
            ),
            SizedBox(
              height: 400,
              width: 300,
              child: ListView.builder(
                itemCount: devices.length,
                itemBuilder: (context, index) => Column(
                  children: <Widget>[
                    SizedBox(
                      height: 10,
                    ),
                    RaisedButton(
                      color: Colors.white,
                      shape: new RoundedRectangleBorder(
                        borderRadius: BorderRadius.only(
                          topRight: Radius.circular(40.0),
                          bottomRight: Radius.circular(40.0),
                          bottomLeft: Radius.circular(40.0),
                          topLeft: Radius.circular(40.0),
                        ),
                        //side: BorderSide(color: Colors.red)
                      ),
                      child: ListTile(
                        title: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              '${devices[index].device.name}',
                              style: TextStyle(
                                  fontWeight: FontWeight.bold,
                                  decoration: TextDecoration.underline),
                            ),
                            Text(
                              '${devices[index].device.id}',
                              style: TextStyle(
                                color: Colors.grey,
                              ),
                            ),
                          ],
                        ),
                      ),
                      onPressed: () {
                        connectDevice(
                          devices[index].device.name,
                          devices[index].device.id.toString(),
                          devices[index].device.id.toString(),
                        );
                      },
                    ),
                  ],
                ),
              ),
            ),
            SizedBox(
              height: 30,
            ),
          ],
        ),
      ),
      // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
