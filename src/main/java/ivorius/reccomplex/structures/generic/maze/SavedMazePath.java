/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://lukas.axxim.net
 */

package ivorius.reccomplex.structures.generic.maze;

import com.google.gson.*;
import ivorius.ivtoolkit.maze.MazePath;
import ivorius.ivtoolkit.maze.MazeRoom;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.reccomplex.json.JsonUtils;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

/**
 * Created by lukas on 14.04.15.
 */
public class SavedMazePath implements NBTCompoundObject
{
    public MazeRoom sourceRoom;
    public int pathDimension;
    public boolean pathGoesUp;

    public SavedMazePath()
    {
    }

    public SavedMazePath(int pathDimension, MazeRoom sourceRoom, boolean pathGoesUp)
    {
        this.sourceRoom = sourceRoom;
        this.pathDimension = pathDimension;
        this.pathGoesUp = pathGoesUp;
    }

    public static SavedMazePath fromPath(MazePath path, boolean pathGoesUp)
    {
        return new SavedMazePath(path.getPathDimension(), pathGoesUp ? path.getSourceRoom() : path.getDestinationRoom(), pathGoesUp);
    }

    public MazePath toPath()
    {
        return MazePath.fromRoom(pathDimension, sourceRoom, pathGoesUp);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        sourceRoom = new MazeRoom(compound.getCompoundTag("source"));
        pathDimension = compound.getInteger("pathDimension");
        pathGoesUp = compound.getBoolean("pathGoesUp");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("source", sourceRoom.storeInNBT());
        compound.setInteger("pathDimension", pathDimension);
        compound.setBoolean("pathGoesUp", pathGoesUp);
    }

    public static class Serializer implements JsonSerializer<SavedMazePath>, JsonDeserializer<SavedMazePath>
    {
        @Override
        public SavedMazePath deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject jsonObject = JsonUtils.getJsonElementAsJsonObject(json, "MazeRoom");

            MazeRoom src = context.deserialize(jsonObject.get("source"), MazeRoom.class);
            int pathDimension = JsonUtils.getJsonObjectIntegerFieldValue(jsonObject, "pathDimension");
            boolean pathGoesUp = JsonUtils.getJsonObjectBooleanFieldValue(jsonObject, "pathGoesUp");

            return new SavedMazePath(pathDimension, src, pathGoesUp);
        }

        @Override
        public JsonElement serialize(SavedMazePath src, Type typeOfSrc, JsonSerializationContext context)
        {
            JsonObject jsonObject = new JsonObject();

            jsonObject.add("source", context.serialize(src.sourceRoom));
            jsonObject.addProperty("pathDimension", src.pathDimension);
            jsonObject.addProperty("pathGoesUp", src.pathGoesUp);

            return jsonObject;
        }
    }
}