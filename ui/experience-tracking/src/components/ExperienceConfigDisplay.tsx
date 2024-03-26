import { ExperienceConfig, ExperienceRole } from "../data/leaderboard";
import { RoleDisplay } from "./RoleDisplay";
import { useEffect, useState } from "react";

export const ExperienceConfigDisplay = ({ serverId }: { serverId: bigint }) => {

    const [roles, setRoles] = useState<ExperienceRole[]>([])
    const [hasError, setError] = useState(false)
    const [isOpen, setOpen] = useState(true);
    async function loadConfig() {
        try {
            const configResponse = await fetch(`https://sissi-dev.sheldan.dev/experience/v1/leaderboards/${serverId}/config`)
            let configObj: ExperienceConfig = await configResponse.json();
            const roles = configObj.roles;
            setRoles(roles)
        } catch (error) {
            console.log(error)
            setError(true)
        }
    }

    function toggleOpen() {
        setOpen(!isOpen);
    }

    useEffect(() => {
        loadConfig()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    return (
        <>
            {!hasError ?
                <div className="bg-gray-900 p-2 lg:p-4 w-11/12 mx-auto rounded-lg">
                    <div onClick={toggleOpen} className="w-full flex justify-between items-center p-2 px-4">
                        <h2 className="text-xl lg:text-2xl font-extrabold leading-none tracking-tight text-gray-100">Role Config</h2>
                        <button><svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" className={`w-6 h-6 stroke-white stroke-2 duration-300 ${isOpen ? "rotate-180" : ""}`}>
                            <path stroke-linecap="round" stroke-linejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5" />
                        </svg>
                        </button>
                    </div>
                    <div className={`grid grid-cols-1 ${isOpen ? 'grid-rows-[1fr]' : 'grid-rows-[0fr]'} overflow-hidden duration-300`}>
                        <div className="w-full gap-3 min-h-0 overflow-hidden flex flex-col items-start">
                            {roles.map(role =>
                                <div key={role.role.id} className="border-gray-700 flex gap-4 p-4 items-center flex-row-reverse">
                                    <p className="">
                                        <RoleDisplay role={role.role} />
                                    </p>
                                    <p className="font-bold text-lg text-gray-200">
                                        Level {role.level}
                                    </p>
                                </div>)}
                        </div>
                    </div>

                    {/* <table className="w-full text-gray-400">
                        <thead
                            className="text-xs uppercase bg-gray-700 text-gray-400">
                            <tr>
                                <th className="px-6 py-3 w-1/2">Role</th>
                                <th className="px-6 py-3 w-1/8">Level</th>
                            </tr>
                        </thead>
                        <tbody>
                            {roles.map(role =>
                                <tr key={role.role.id} className="border-b bg-gray-800 border-gray-700">
                                    <td className="px-6 py-4">
                                        <RoleDisplay role={role.role} />
                                    </td>
                                    <td className="px-6 py-4 text-center">
                                        {role.level}
                                    </td>
                                </tr>)}
                        </tbody>
                    </table>
                    {roles.length === 0 ?
                        <div className="w-full flex items-center justify-center">
                            <span className="text-gray-400">No roles</span>
                        </div> : ''} */}
                </div>
                : ''}
        </>
    );
}