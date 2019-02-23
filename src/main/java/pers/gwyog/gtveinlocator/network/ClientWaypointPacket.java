package pers.gwyog.gtveinlocator.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import java.nio.charset.Charset;
import pers.gwyog.gtveinlocator.util.GTVeinNameHelper;

public class ClientWaypointPacket implements IMessage {

    public String wpName;
    public int type;
    public int posX;
    public int posY;
    public int posZ;
    public int dimId;

    public ClientWaypointPacket() {
    }

    public ClientWaypointPacket(String wpName, int type, int posX, int posY, int posZ, int dimId) {
        this.wpName = wpName;
        this.type = type;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.dimId = dimId;
    }

    public ClientWaypointPacket(String wpName, int posX, int posY, int posZ, int dimId) {
        this.wpName = wpName;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.dimId = dimId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = buf.readInt();
        if (type == 0) {
            wpName = GTVeinNameHelper.getName(buf.readByte());
        } else {
            short lenth = buf.readShort();
            byte[] ss = new byte[lenth];
            buf.readBytes(ss);
            wpName = new String(ss, Charset.forName("UTF-8"));
        }
        posX = buf.readInt();
        posY = buf.readInt();
        posZ = buf.readInt();
        dimId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type);
        if (type == 0) {
            buf.writeByte(GTVeinNameHelper.getIndex(wpName));
        } else {
            buf.writeShort(wpName.getBytes(Charset.forName("UTF-8")).length);
            buf.writeBytes(wpName.getBytes(Charset.forName("UTF-8")));
        }
        buf.writeInt(posX);
        buf.writeInt(posY);
        buf.writeInt(posZ);
        buf.writeInt(dimId);
    }

}
