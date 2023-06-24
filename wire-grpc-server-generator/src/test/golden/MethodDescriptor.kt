package routeguide

import io.grpc.MethodDescriptor
import kotlin.jvm.Volatile

public class RouteGuideWireGrpc {
  @Volatile
  private var getGetFeatureMethod: MethodDescriptor<Point, Feature>? = null

  public fun getGetFeatureMethod(): MethodDescriptor<Point, Feature> {
    var result: MethodDescriptor<Point, Feature>? = getGetFeatureMethod
    if (result == null) {
      synchronized(RouteGuideWireGrpc::class) {
        result = getGetFeatureMethod
        if (result == null) {
          getGetFeatureMethod = MethodDescriptor.newBuilder<Point, Feature>()
            .setType(MethodDescriptor.MethodType.UNARY)
            .setFullMethodName(
              MethodDescriptor.generateFullMethodName(
                "routeguide.RouteGuide", "GetFeature"
              )
            )
            .setSampledToLocalTracing(true)
            .setRequestMarshaller(RouteGuideImplBase.PointMarshaller())
            .setResponseMarshaller(RouteGuideImplBase.FeatureMarshaller())
            .build()
        }
      }
    }
    return getGetFeatureMethod!!
  }
}
