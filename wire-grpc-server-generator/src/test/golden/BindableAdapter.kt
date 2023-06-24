package routeguide

import com.squareup.wire.kotlin.grpcserver.MessageSinkAdapter
import com.squareup.wire.kotlin.grpcserver.MessageSourceAdapter
import io.grpc.stub.StreamObserver
import java.util.concurrent.ExecutorService

public class RouteGuideWireGrpc {
  public class BindableAdapter(
    private val streamExecutor: ExecutorService,
    private val GetFeature: () -> RouteGuideGetFeatureBlockingServer,
    private val ListFeatures: () -> RouteGuideListFeaturesBlockingServer,
    private val RecordRoute: () -> RouteGuideRecordRouteBlockingServer,
    private val RouteChat: () -> RouteGuideRouteChatBlockingServer,
  ) : RouteGuideWireGrpc.RouteGuideImplBase() {
    override fun GetFeature(request: Point, response: StreamObserver<Feature>) {
      response.onNext(GetFeature().GetFeature(request))
      response.onCompleted()
    }

    override fun ListFeatures(request: Rectangle, response: StreamObserver<Feature>) {
      ListFeatures().ListFeatures(request, MessageSinkAdapter(response))
    }

    override fun RecordRoute(response: StreamObserver<RouteSummary>): StreamObserver<Point> {
      val requestStream = MessageSourceAdapter<Point>()
      streamExecutor.submit {
        response.onNext(RecordRoute().RecordRoute(requestStream))
        response.onCompleted()
      }
      return requestStream
    }

    override fun RouteChat(response: StreamObserver<RouteNote>): StreamObserver<RouteNote> {
      val requestStream = MessageSourceAdapter<RouteNote>()
      streamExecutor.submit {
        RouteChat().RouteChat(requestStream, MessageSinkAdapter(response))
      }
      return requestStream
    }
  }
}
