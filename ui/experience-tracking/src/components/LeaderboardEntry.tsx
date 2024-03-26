import { ExperienceMember } from "../data/leaderboard";
import { RoleDisplay } from "./RoleDisplay";
import createStyle from "../utils/styleUtils";

export const LeaderboardEntry = ({ member }: { member: ExperienceMember }) => {
    const userHasRole = member.role !== null;
    const memberExists = member.member !== null;
    const nameColor = userHasRole ? createStyle(member.role!) : ''
    let memberDisplay = memberExists ? <>
        <img alt={member.member!.name} src={member.member!.avatarUrl}
            className="object-cover w-8 lg:w-16 aspect-square rounded-full" />
        <span className="align-middle" style={{ color: nameColor }}>{member.member!.name}</span>
    </> : <>{member.id}</>;
    return (
        <>
            <div className="flex rounded-2xl items-center bg-slate-700/30 text-white p-2">
                <p className="lg:text-xl lg:text-center font-bold w-1/6">#1</p>
                <p className="w-3/6 font-medium whitespace-nowrap flex items-center gap-3">{memberDisplay}</p>
                <p className="px-6 py-4 text-center w-1/5">{member.experience.toLocaleString()}</p>
                <p className="px-6 py-4 text-center w-1/5">{member.messages.toLocaleString()}</p>
                <p className="px-6 py-4 text-center w-1/5">{member.level.toString()}</p>
            </div>

        </>
    );
}