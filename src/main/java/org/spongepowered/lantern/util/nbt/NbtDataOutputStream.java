package org.spongepowered.lantern.util.nbt;

import com.google.common.collect.Maps;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * This class writes NBT, or Named Binary Tag, objects to an
 * underlying {@link OutputStream} from a {@link DataContainer}.
 */
public class NbtDataOutputStream implements Closeable {

    /**
     * The output stream.
     */
    private final DataOutputStream os;

    /**
     * Creates a new NBTOutputStream, which will write data to the
     * specified underlying output stream. This assumes the output stream
     * should be compressed with GZIP.
     * @param os The output stream.
     * @throws IOException if an I/O error occurs.
     */
    public NbtDataOutputStream(OutputStream os) throws IOException {
        this(os, true);
    }

    /**
     * Creates a new NBTOutputStream, which will write data to the
     * specified underlying output stream. A flag indicates if the output
     * should be compressed with GZIP or not.
     * @param os The output stream.
     * @param compressed A flag that indicates if the output should be compressed.
     * @throws IOException if an I/O error occurs.
     */
    public NbtDataOutputStream(OutputStream os, boolean compressed) throws IOException {
        this.os = new DataOutputStream(compressed ? new GZIPOutputStream(os) : os);
    }

    /**
     * Write a tag with a blank name (the root tag) to the stream.
     * @param data The tag to write.
     * @throws IOException if an I/O error occurs.
     */
    public void write(DataView data) throws IOException {
        writeTag("", data);
    }

    /**
     * Write a tag with a name.
     * @param name The name to give the written tag.
     * @param data The tag to write.
     * @throws IOException if an I/O error occurs.
     */
    private void writeTag(String name, Object data) throws IOException {
        TagType type = getType(data);
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);

        if (type == TagType.END) {
            throw new IOException("Named TAG_End not permitted.");
        }

        os.writeByte(type.getId());
        os.writeShort(nameBytes.length);
        os.write(nameBytes);

        writeTagPayload(data);
    }

    /**
     * Writes tag payload.
     * @param object The tag.
     * @throws IOException if an I/O error occurs.
     */
    @SuppressWarnings("unchecked")
    private void writeTagPayload(Object object) throws IOException {
        TagType type = getType(object);
        byte[] bytes;

        switch (type) {
            case BYTE:
                os.writeByte((byte) object);
                break;

            case SHORT:
                os.writeShort((short) object);
                break;

            case INT:
                os.writeInt((int) object);
                break;

            case LONG:
                os.writeLong((long) object);
                break;

            case FLOAT:
                os.writeFloat((float) object);
                break;

            case DOUBLE:
                os.writeDouble((double) object);
                break;

            case BYTE_ARRAY:
                bytes = (byte[]) object;
                os.writeInt(bytes.length);
                os.write(bytes);
                break;

            case STRING:
                bytes = ((String) object).getBytes(StandardCharsets.UTF_8);
                os.writeShort(bytes.length);
                os.write(bytes);
                break;

            case LIST:
                List list = (List) object;

                TagType childType;
                if(list.isEmpty()) {
                    childType = TagType.END;
                } else {
                    childType = getType(list.get(0));
                    for(Object child : list) {
                        if(childType != getType(child)) {
                            throw new IllegalArgumentException("Tag list doesn't not contains elements of only one type:" + object.toString());
                        }
                    }
                }

                os.writeByte(childType.getId());
                os.writeInt(list.size());
                for (Object child : list) {
                    writeTagPayload(child);
                }
                break;

            case COMPOUND:
                if(object instanceof DataSerializable) {
                    object = ((DataSerializable) object).toContainer();
                }

                Map<String, Object> map;
                if(object instanceof DataView) {
                    map = Maps.newHashMap();
                    for(DataQuery query : ((DataView) object).getValues(false).keySet()) {
                        map.put(query.toString(), ((DataView) object).get(query));
                    }
                } else {
                    map = (Map<String, Object>) object;
                }
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    writeTag(entry.getKey(), entry.getValue());
                }
                os.writeByte(TagType.END.getId());
                break;

            case INT_ARRAY:
                int[] ints = (int[]) object;
                os.writeInt(ints.length);
                for (int value : ints) {
                    os.writeInt(value);
                }
                break;

            default:
                throw new IOException("Invalid tag type: " + type);
        }
    }

    public TagType getType(Object object) {
        if (object instanceof Byte) {
            return TagType.BYTE;
        } else if (object instanceof Short) {
            return TagType.SHORT;
        } else if (object instanceof Integer) {
            return TagType.INT;
        } else if (object instanceof Long) {
            return TagType.LONG;
        } else if (object instanceof Float) {
            return TagType.FLOAT;
        } else if (object instanceof Double) {
            return TagType.DOUBLE;
        } else if (object instanceof String) {
            return TagType.STRING;
        } else if (object.getClass().isArray()) {
            if (object instanceof byte[]) {
                return TagType.BYTE_ARRAY;
            } else if (object instanceof Byte[]) {
                return TagType.BYTE_ARRAY;
            } else if (object instanceof int[]) {
                return TagType.INT_ARRAY;
            } else if (object instanceof Integer[]) {
                return TagType.INT_ARRAY;
            }
        } else if (object instanceof List) {
            return TagType.LIST;
        } else if (object instanceof Map) {
            return TagType.COMPOUND;
        } else if (object instanceof DataView) {
            return TagType.COMPOUND;
        } else if (object instanceof DataSerializable) {
            return TagType.COMPOUND;
        }
        throw new IllegalArgumentException("Unable to find type of object: " + object.toString());
    }

    @Override
    public void close() throws IOException {
        os.close();
    }

}
