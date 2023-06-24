// Code generated by Wire protocol buffer compiler, do not edit.
// Source: com.squareup.wire.whiteboard.WhiteboardCommand in com/squareup/wire/whiteboard/whiteboard.proto
package com.squareup.wire.whiteboard

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.ReverseProtoWriter
import com.squareup.wire.Syntax
import com.squareup.wire.Syntax.PROTO_2
import com.squareup.wire.WireField
import com.squareup.wire.`internal`.countNonNull
import com.squareup.wire.`internal`.missingRequiredFields
import kotlin.Any
import kotlin.AssertionError
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.DeprecationLevel
import kotlin.Int
import kotlin.Long
import kotlin.Nothing
import kotlin.String
import kotlin.Unit
import kotlin.jvm.JvmField
import okio.ByteString

public class WhiteboardCommand(
  @field:WireField(
    tag = 1,
    adapter = "com.squareup.wire.whiteboard.WhiteboardCommand${'$'}AddPoint#ADAPTER",
    oneofName = "one_whiteboard_command",
  )
  public val add_point: AddPoint? = null,
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.whiteboard.WhiteboardCommand${'$'}ClearBoard#ADAPTER",
    oneofName = "one_whiteboard_command",
  )
  public val clear_board: ClearBoard? = null,
  unknownFields: ByteString = ByteString.EMPTY,
) : Message<WhiteboardCommand, Nothing>(ADAPTER, unknownFields) {
  init {
    require(countNonNull(add_point, clear_board) <= 1) {
      "At most one of add_point, clear_board may be non-null"
    }
  }

  @Deprecated(
    message = "Shouldn't be used in Kotlin",
    level = DeprecationLevel.HIDDEN,
  )
  public override fun newBuilder(): Nothing = throw
      AssertionError("Builders are deprecated and only available in a javaInterop build; see https://square.github.io/wire/wire_compiler/#kotlin")

  public override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is WhiteboardCommand) return false
    if (unknownFields != other.unknownFields) return false
    if (add_point != other.add_point) return false
    if (clear_board != other.clear_board) return false
    return true
  }

  public override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + (add_point?.hashCode() ?: 0)
      result = result * 37 + (clear_board?.hashCode() ?: 0)
      super.hashCode = result
    }
    return result
  }

  public override fun toString(): String {
    val result = mutableListOf<String>()
    if (add_point != null) result += """add_point=$add_point"""
    if (clear_board != null) result += """clear_board=$clear_board"""
    return result.joinToString(prefix = "WhiteboardCommand{", separator = ", ", postfix = "}")
  }

  public fun copy(
    add_point: AddPoint? = this.add_point,
    clear_board: ClearBoard? = this.clear_board,
    unknownFields: ByteString = this.unknownFields,
  ): WhiteboardCommand = WhiteboardCommand(add_point, clear_board, unknownFields)

  public companion object {
    @JvmField
    public val ADAPTER: ProtoAdapter<WhiteboardCommand> = object : ProtoAdapter<WhiteboardCommand>(
      FieldEncoding.LENGTH_DELIMITED, 
      WhiteboardCommand::class, 
      "type.googleapis.com/com.squareup.wire.whiteboard.WhiteboardCommand", 
      PROTO_2, 
      null, 
      "com/squareup/wire/whiteboard/whiteboard.proto"
    ) {
      public override fun encodedSize(`value`: WhiteboardCommand): Int {
        var size = value.unknownFields.size
        size += AddPoint.ADAPTER.encodedSizeWithTag(1, value.add_point)
        size += ClearBoard.ADAPTER.encodedSizeWithTag(2, value.clear_board)
        return size
      }

      public override fun encode(writer: ProtoWriter, `value`: WhiteboardCommand): Unit {
        AddPoint.ADAPTER.encodeWithTag(writer, 1, value.add_point)
        ClearBoard.ADAPTER.encodeWithTag(writer, 2, value.clear_board)
        writer.writeBytes(value.unknownFields)
      }

      public override fun encode(writer: ReverseProtoWriter, `value`: WhiteboardCommand): Unit {
        writer.writeBytes(value.unknownFields)
        ClearBoard.ADAPTER.encodeWithTag(writer, 2, value.clear_board)
        AddPoint.ADAPTER.encodeWithTag(writer, 1, value.add_point)
      }

      public override fun decode(reader: ProtoReader): WhiteboardCommand {
        var add_point: AddPoint? = null
        var clear_board: ClearBoard? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> add_point = AddPoint.ADAPTER.decode(reader)
            2 -> clear_board = ClearBoard.ADAPTER.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return WhiteboardCommand(
          add_point = add_point,
          clear_board = clear_board,
          unknownFields = unknownFields
        )
      }

      public override fun redact(`value`: WhiteboardCommand): WhiteboardCommand = value.copy(
        add_point = value.add_point?.let(AddPoint.ADAPTER::redact),
        clear_board = value.clear_board?.let(ClearBoard.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }

  public class AddPoint(
    @field:WireField(
      tag = 1,
      adapter = "com.squareup.wire.whiteboard.Point#ADAPTER",
      label = WireField.Label.REQUIRED,
    )
    public val point: Point,
    unknownFields: ByteString = ByteString.EMPTY,
  ) : Message<AddPoint, Nothing>(ADAPTER, unknownFields) {
    @Deprecated(
      message = "Shouldn't be used in Kotlin",
      level = DeprecationLevel.HIDDEN,
    )
    public override fun newBuilder(): Nothing = throw
        AssertionError("Builders are deprecated and only available in a javaInterop build; see https://square.github.io/wire/wire_compiler/#kotlin")

    public override fun equals(other: Any?): Boolean {
      if (other === this) return true
      if (other !is AddPoint) return false
      if (unknownFields != other.unknownFields) return false
      if (point != other.point) return false
      return true
    }

    public override fun hashCode(): Int {
      var result = super.hashCode
      if (result == 0) {
        result = unknownFields.hashCode()
        result = result * 37 + point.hashCode()
        super.hashCode = result
      }
      return result
    }

    public override fun toString(): String {
      val result = mutableListOf<String>()
      result += """point=$point"""
      return result.joinToString(prefix = "AddPoint{", separator = ", ", postfix = "}")
    }

    public fun copy(point: Point = this.point, unknownFields: ByteString = this.unknownFields):
        AddPoint = AddPoint(point, unknownFields)

    public companion object {
      @JvmField
      public val ADAPTER: ProtoAdapter<AddPoint> = object : ProtoAdapter<AddPoint>(
        FieldEncoding.LENGTH_DELIMITED, 
        AddPoint::class, 
        "type.googleapis.com/com.squareup.wire.whiteboard.WhiteboardCommand.AddPoint", 
        PROTO_2, 
        null, 
        "com/squareup/wire/whiteboard/whiteboard.proto"
      ) {
        public override fun encodedSize(`value`: AddPoint): Int {
          var size = value.unknownFields.size
          size += Point.ADAPTER.encodedSizeWithTag(1, value.point)
          return size
        }

        public override fun encode(writer: ProtoWriter, `value`: AddPoint): Unit {
          Point.ADAPTER.encodeWithTag(writer, 1, value.point)
          writer.writeBytes(value.unknownFields)
        }

        public override fun encode(writer: ReverseProtoWriter, `value`: AddPoint): Unit {
          writer.writeBytes(value.unknownFields)
          Point.ADAPTER.encodeWithTag(writer, 1, value.point)
        }

        public override fun decode(reader: ProtoReader): AddPoint {
          var point: Point? = null
          val unknownFields = reader.forEachTag { tag ->
            when (tag) {
              1 -> point = Point.ADAPTER.decode(reader)
              else -> reader.readUnknownField(tag)
            }
          }
          return AddPoint(
            point = point ?: throw missingRequiredFields(point, "point"),
            unknownFields = unknownFields
          )
        }

        public override fun redact(`value`: AddPoint): AddPoint = value.copy(
          point = Point.ADAPTER.redact(value.point),
          unknownFields = ByteString.EMPTY
        )
      }

      private const val serialVersionUID: Long = 0L
    }
  }

  public class ClearBoard(
    unknownFields: ByteString = ByteString.EMPTY,
  ) : Message<ClearBoard, Nothing>(ADAPTER, unknownFields) {
    @Deprecated(
      message = "Shouldn't be used in Kotlin",
      level = DeprecationLevel.HIDDEN,
    )
    public override fun newBuilder(): Nothing = throw
        AssertionError("Builders are deprecated and only available in a javaInterop build; see https://square.github.io/wire/wire_compiler/#kotlin")

    public override fun equals(other: Any?): Boolean {
      if (other === this) return true
      if (other !is ClearBoard) return false
      if (unknownFields != other.unknownFields) return false
      return true
    }

    public override fun hashCode(): Int = unknownFields.hashCode()

    public override fun toString(): String = "ClearBoard{}"

    public fun copy(unknownFields: ByteString = this.unknownFields): ClearBoard =
        ClearBoard(unknownFields)

    public companion object {
      @JvmField
      public val ADAPTER: ProtoAdapter<ClearBoard> = object : ProtoAdapter<ClearBoard>(
        FieldEncoding.LENGTH_DELIMITED, 
        ClearBoard::class, 
        "type.googleapis.com/com.squareup.wire.whiteboard.WhiteboardCommand.ClearBoard", 
        PROTO_2, 
        null, 
        "com/squareup/wire/whiteboard/whiteboard.proto"
      ) {
        public override fun encodedSize(`value`: ClearBoard): Int {
          var size = value.unknownFields.size
          return size
        }

        public override fun encode(writer: ProtoWriter, `value`: ClearBoard): Unit {
          writer.writeBytes(value.unknownFields)
        }

        public override fun encode(writer: ReverseProtoWriter, `value`: ClearBoard): Unit {
          writer.writeBytes(value.unknownFields)
        }

        public override fun decode(reader: ProtoReader): ClearBoard {
          val unknownFields = reader.forEachTag(reader::readUnknownField)
          return ClearBoard(
            unknownFields = unknownFields
          )
        }

        public override fun redact(`value`: ClearBoard): ClearBoard = value.copy(
          unknownFields = ByteString.EMPTY
        )
      }

      private const val serialVersionUID: Long = 0L
    }
  }
}
